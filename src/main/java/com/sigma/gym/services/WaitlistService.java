package com.sigma.gym.services;

import com.sigma.gym.DTOs.*;
import com.sigma.gym.config.WaitlistProperties;
import com.sigma.gym.entity.*;
import com.sigma.gym.events.*;
import com.sigma.gym.exceptions.WaitlistException;
import com.sigma.gym.exceptions.ClassSessionNotFoundException;
import com.sigma.gym.exceptions.UserNotFoundException;
import com.sigma.gym.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "sigmagym.features.waitlist", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class WaitlistService {

    private final WaitlistEntryRepository waitlistRepository;
    private final ClassSessionRepository classSessionRepository;
    private final PromotionAuditRepository promotionAuditRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final MembershipRepository membershipRepository;
    private final WaitlistProperties waitlistProperties;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Join waitlist for a class session
     */
    @Transactional
    public JoinWaitlistResponseDTO joinWaitlist(Long classSessionId, Long userId, String note) {
        log.info("User {} attempting to join waitlist for class {}", userId, classSessionId);

        // Get class session with lock
        ClassSessionEntity classSession = classSessionRepository.findByIdForUpdate(classSessionId)
            .orElseThrow(() -> new ClassSessionNotFoundException(classSessionId));

        // Get user
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));

        // Validate timing - check if class starts too soon
        if (isClassStartingSoon(classSession)) {
            return JoinWaitlistResponseDTO.error(
                String.format("Cannot join waitlist - class starts in less than %d minutes", 
                    waitlistProperties.getJoinWaitlistCutoffMinutes()),
                "CUTOFF_REACHED"
            );
        }

        // Check if user already has a booking for this class
        if (bookingRepository.existsByUserIdAndClassSessionIdAndStatus(userId, classSessionId, BookingStatus.CONFIRMED)) {
            return JoinWaitlistResponseDTO.error(
                "You already have a confirmed booking for this class",
                "ALREADY_BOOKED"
            );
        }

        // Check if user already in waitlist
        List<WaitlistEntryEntity.WaitlistStatus> activeStatuses = Arrays.asList(
            WaitlistEntryEntity.WaitlistStatus.QUEUED,
            WaitlistEntryEntity.WaitlistStatus.PROMOTED
        );
        
        if (waitlistRepository.existsByClassSession_IdAndUserIdAndStatusIn(classSessionId, userId, activeStatuses)) {
            return JoinWaitlistResponseDTO.error(
                "You are already in the waitlist for this class",
                "ALREADY_IN_WAITLIST"
            );
        }

        // Check if class has capacity
        if (classSession.hasCapacity()) {
            return JoinWaitlistResponseDTO.error(
                "Class has available spots - please book directly instead of joining waitlist",
                "CLASS_HAS_CAPACITY"
            );
        }

        // Check waitlist size limits
        int currentQueueSize = waitlistRepository.countByClassSession_IdAndStatus(
            classSessionId, WaitlistEntryEntity.WaitlistStatus.QUEUED);
            
        if (currentQueueSize >= waitlistProperties.getWaitlistMaxSize()) {
            return JoinWaitlistResponseDTO.error(
                String.format("Waitlist is full (maximum %d entries)", waitlistProperties.getWaitlistMaxSize()),
                "WAITLIST_FULL"
            );
        }

        // Validate membership status
        validateMembershipForWaitlist(user);

        // Get next position
        Integer maxPosition = waitlistRepository.findMaxPositionForClass(classSessionId);
        Integer newPosition = (maxPosition != null ? maxPosition : 0) + 1;

        // Create waitlist entry
        WaitlistEntryEntity entry = WaitlistEntryEntity.builder()
            .classSession(classSession)
            .user(user)
            .position(newPosition)
            .status(WaitlistEntryEntity.WaitlistStatus.QUEUED)
            .note(note)
            .createdAt(LocalDateTime.now())
            .build();

        entry = waitlistRepository.save(entry);

        log.info("User {} successfully joined waitlist for class {} at position {}", 
            userId, classSessionId, newPosition);

        return JoinWaitlistResponseDTO.success(
            WaitlistEntryDTO.fromEntity(entry),
            currentQueueSize + 1
        );
    }

    /**
     * Leave waitlist
     */
    @Transactional
    public void leaveWaitlist(Long entryId, Long userId) {
        log.info("User {} attempting to leave waitlist entry {}", userId, entryId);

        WaitlistEntryEntity entry = waitlistRepository.findByIdAndUserId(entryId, userId)
            .orElseThrow(() -> new WaitlistException("Waitlist entry not found or not owned by user", "ENTRY_NOT_FOUND"));

        if (!entry.isQueued() && !entry.isPromoted()) {
            throw new WaitlistException("Cannot leave waitlist - entry is not active", "INVALID_STATUS");
        }

        Long classSessionId = entry.getClassSessionId();
        Integer position = entry.getPosition();

        // Update entry status
        entry.setStatus(WaitlistEntryEntity.WaitlistStatus.LEFT);
        entry.setUpdatedAt(LocalDateTime.now());
        waitlistRepository.save(entry);

        // Update positions of entries after this one
        if (entry.isQueued()) {
            waitlistRepository.updatePositionsAfterLeave(classSessionId, position);
        }

        // If this was a promoted entry with active hold, promote next
        if (entry.isPromoted() && entry.canConfirm()) {
            promoteNext(classSessionId);
        }

        log.info("User {} successfully left waitlist entry {}", userId, entryId);
    }

    /**
     * Confirm promotion (user accepts their spot)
     */
    @Transactional
    public BookingDTO confirmPromotion(Long entryId, Long userId) {
        log.info("User {} attempting to confirm promotion for entry {}", userId, entryId);

        // Get entry with lock
        WaitlistEntryEntity entry = waitlistRepository.findByIdForUpdate(entryId)
            .orElseThrow(() -> new WaitlistException("Waitlist entry not found", "ENTRY_NOT_FOUND"));

        // Validate ownership
        if (!entry.getUserId().equals(userId)) {
            throw new WaitlistException("Not authorized to confirm this entry", "UNAUTHORIZED");
        }

        // Validate entry can be confirmed
        if (!entry.canConfirm()) {
            if (entry.isHoldExpired()) {
                throw new WaitlistException("Hold period has expired", "HOLD_EXPIRED");
            }
            throw new WaitlistException("Entry cannot be confirmed in current status", "INVALID_STATUS");
        }

        // Get class session with lock
        ClassSessionEntity classSession = classSessionRepository.findByIdForUpdate(entry.getClassSessionId())
            .orElseThrow(() -> new ClassSessionNotFoundException(entry.getClassSessionId()));

        // Validate membership status at confirmation time
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        
        validateMembershipForConfirmation(user);

        // Check if class still has capacity
        if (!classSession.hasCapacity()) {
            throw new WaitlistException("Class is now full - cannot confirm", "CLASS_FULL");
        }

        // Create booking
        BookingEntity booking = BookingEntity.builder()
            .user(user)
            .classSession(classSession)
            .status(BookingStatus.CONFIRMED)
            .createdAt(LocalDateTime.now())
            .notes("Confirmed from waitlist position " + entry.getPosition())
            .build();

        booking = bookingRepository.save(booking);

        // Update class capacity
        classSession.incrementBookedCount();
        classSessionRepository.save(classSession);

        // Update waitlist entry
        entry.setStatus(WaitlistEntryEntity.WaitlistStatus.CONFIRMED);
        entry.setUpdatedAt(LocalDateTime.now());
        waitlistRepository.save(entry);

        // Create audit record
        createPromotionAudit(entry, PromotionAuditEntity.PromotionAction.CONFIRMED, 
            "User confirmed promotion within hold window", LocalDateTime.now());

        // Publish event
        publishWaitlistConfirmedEvent(entry, booking);

        log.info("User {} successfully confirmed promotion for entry {} and got booking {}", 
            userId, entryId, booking.getId());

        return BookingDTO.fromEntity(booking);
    }

    /**
     * Promote next user in waitlist (called when spot becomes available)
     */
    @Transactional
    public void promoteNext(Long classSessionId) {
        log.info("Attempting to promote next user for class {}", classSessionId);

        // Get class session with lock
        ClassSessionEntity classSession = classSessionRepository.findByIdForUpdate(classSessionId)
            .orElseThrow(() -> new ClassSessionNotFoundException(classSessionId));

        // Check if there's actually a spot available
        if (!classSession.hasCapacity()) {
            log.info("No capacity available for class {} - skipping promotion", classSessionId);
            return;
        }

        // Find next eligible entry
        Optional<WaitlistEntryEntity> nextEntryOpt = waitlistRepository
            .findTopByClassSession_IdAndStatusOrderByPositionAscCreatedAtAsc(
                classSessionId, WaitlistEntryEntity.WaitlistStatus.QUEUED);

        if (nextEntryOpt.isEmpty()) {
            log.info("No queued entries found for class {} - no promotion needed", classSessionId);
            return;
        }

        WaitlistEntryEntity nextEntry = nextEntryOpt.get();
        
        // Check if user's membership allows confirmation
        if (!canUserConfirmPromotion(nextEntry.getUser())) {
            log.info("User {} cannot confirm promotion due to membership status - skipping to next", 
                nextEntry.getUserId());
            
            // Mark as skipped and try next
            nextEntry.setStatus(WaitlistEntryEntity.WaitlistStatus.EXPIRED);
            nextEntry.setUpdatedAt(LocalDateTime.now());
            waitlistRepository.save(nextEntry);
            
            createPromotionAudit(nextEntry, PromotionAuditEntity.PromotionAction.SKIPPED, 
                "Skipped due to membership status", null);
                
            // Recursively try next user
            promoteNext(classSessionId);
            return;
        }

        // Promote the user
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime holdUntil = now.plusMinutes(waitlistProperties.getPromotionHoldMinutes());

        nextEntry.setStatus(WaitlistEntryEntity.WaitlistStatus.PROMOTED);
        nextEntry.setPromotedAt(now);
        nextEntry.setHoldUntil(holdUntil);
        nextEntry.setUpdatedAt(now);
        
        waitlistRepository.save(nextEntry);

        // Create audit record
        createPromotionAudit(nextEntry, PromotionAuditEntity.PromotionAction.PROMOTED, 
            "Promoted from waitlist", now);

        // Publish event for notifications
        publishWaitlistPromotedEvent(nextEntry);

        log.info("Successfully promoted user {} from position {} for class {} - hold until {}", 
            nextEntry.getUserId(), nextEntry.getPosition(), classSessionId, holdUntil);
    }

    /**
     * Get user's waitlist entries
     */
    @Transactional(readOnly = true)
    public List<WaitlistEntryDTO> getUserWaitlistEntries(Long userId) {
        List<WaitlistEntryEntity> entries = waitlistRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return entries.stream()
            .map(WaitlistEntryDTO::fromEntity)
            .toList();
    }

    /**
     * Get user's active waitlist entries only
     */
    @Transactional(readOnly = true)
    public List<WaitlistEntryDTO> getUserActiveWaitlistEntries(Long userId) {
        List<WaitlistEntryEntity.WaitlistStatus> activeStatuses = Arrays.asList(
            WaitlistEntryEntity.WaitlistStatus.QUEUED,
            WaitlistEntryEntity.WaitlistStatus.PROMOTED
        );
        
        List<WaitlistEntryEntity> entries = waitlistRepository
            .findByUserIdAndStatusInOrderByCreatedAtDesc(userId, activeStatuses);
            
        return entries.stream()
            .map(WaitlistEntryDTO::fromEntity)
            .toList();
    }

    /**
     * Get waitlist status for a class (admin view)
     */
    @Transactional(readOnly = true)
    public WaitlistStatusDTO getWaitlistStatus(Long classSessionId, Long currentUserId) {
        ClassSessionEntity classSession = classSessionRepository.findById(classSessionId)
            .orElseThrow(() -> new ClassSessionNotFoundException(classSessionId));

        List<WaitlistEntryEntity> allEntries = waitlistRepository
            .findAllByClassSessionIdOrderByPosition(classSessionId);

        List<WaitlistEntryDTO> entryDTOs = allEntries.stream()
            .map(WaitlistEntryDTO::fromEntity)
            .toList();

        int queueSize = waitlistRepository.countByClassSession_IdAndStatus(
            classSessionId, WaitlistEntryEntity.WaitlistStatus.QUEUED);

        // Check current user's status
        WaitlistEntryDTO userEntry = null;
        boolean userInWaitlist = false;
        boolean userHasBooking = false;

        if (currentUserId != null) {
            Optional<WaitlistEntryEntity> userWaitlistEntry = waitlistRepository
                .findByClassSession_IdAndUserIdAndStatus(classSessionId, currentUserId, 
                    WaitlistEntryEntity.WaitlistStatus.QUEUED);
            
            if (userWaitlistEntry.isPresent()) {
                userEntry = WaitlistEntryDTO.fromEntity(userWaitlistEntry.get());
                userInWaitlist = true;
            } else {
                // Check for promoted entry
                Optional<WaitlistEntryEntity> promotedEntry = waitlistRepository
                    .findByClassSession_IdAndUserIdAndStatus(classSessionId, currentUserId, 
                        WaitlistEntryEntity.WaitlistStatus.PROMOTED);
                if (promotedEntry.isPresent()) {
                    userEntry = WaitlistEntryDTO.fromEntity(promotedEntry.get());
                    userInWaitlist = true;
                }
            }

            userHasBooking = bookingRepository.existsByUserIdAndClassSessionIdAndStatus(
                currentUserId, classSessionId, BookingStatus.CONFIRMED);
        }

        return WaitlistStatusDTO.builder()
            .classSessionId(classSessionId)
            .className(classSession.getClassName())
            .totalCapacity(classSession.getCapacity())
            .bookedCount(classSession.getBookedCount())
            .availableSpots(classSession.getAvailableSpots())
            .queueSize(queueSize)
            .isWaitlistOpen(isWaitlistOpen(classSession))
            .entries(entryDTOs)
            .userEntry(userEntry)
            .userInWaitlist(userInWaitlist)
            .userHasBooking(userHasBooking)
            .build();
    }

    /**
     * Process expired holds (called by scheduler)
     */
    @Transactional
    public void processExpiredHolds() {
        List<WaitlistEntryEntity> expiredEntries = waitlistRepository.findExpiredPromotions(LocalDateTime.now());
        
        for (WaitlistEntryEntity entry : expiredEntries) {
            log.info("Processing expired hold for entry {} - user {}", entry.getId(), entry.getUserId());
            
            entry.setStatus(WaitlistEntryEntity.WaitlistStatus.EXPIRED);
            entry.setUpdatedAt(LocalDateTime.now());
            waitlistRepository.save(entry);

            // Create audit record
            createPromotionAudit(entry, PromotionAuditEntity.PromotionAction.EXPIRED, 
                "Hold period expired without confirmation", null);

            // Publish event
            publishWaitlistHoldExpiredEvent(entry);

            // Try to promote next user
            if (waitlistProperties.isAutoPromoteOnFreeSpot()) {
                promoteNext(entry.getClassSessionId());
            }
        }
    }

    // Private helper methods

    private boolean isClassStartingSoon(ClassSessionEntity classSession) {
        return LocalDateTime.now().plusMinutes(waitlistProperties.getJoinWaitlistCutoffMinutes())
            .isAfter(classSession.getStartsAt());
    }

    private boolean isWaitlistOpen(ClassSessionEntity classSession) {
        return !isClassStartingSoon(classSession) && classSession.getIsActive();
    }

    private void validateMembershipForWaitlist(UserEntity user) {
        Optional<MembershipEntity> membership = membershipRepository.findActiveMembershipByUserId(user.getId());
        
        if (membership.isEmpty()) {
            throw new WaitlistException("No active membership found", "NO_ACTIVE_MEMBERSHIP");
        }

        // Note: FROZEN members can join waitlist but cannot confirm promotion
    }

    private void validateMembershipForConfirmation(UserEntity user) {
        Optional<MembershipEntity> membership = membershipRepository.findActiveMembershipByUserId(user.getId());
        
        if (membership.isEmpty()) {
            throw new WaitlistException("No active membership found", "NO_ACTIVE_MEMBERSHIP");
        }

        MembershipEntity membershipEntity = membership.get();
        if (membershipEntity.getStatus() == MembershipEntity.MembershipStatus.FROZEN) {
            throw new WaitlistException("Cannot confirm - membership is frozen", "MEMBERSHIP_FROZEN");
        }
    }

    private boolean canUserConfirmPromotion(UserEntity user) {
        Optional<MembershipEntity> membership = membershipRepository.findActiveMembershipByUserId(user.getId());
        
        return membership.isPresent() && 
               membership.get().getStatus() != MembershipEntity.MembershipStatus.FROZEN;
    }

    private void createPromotionAudit(WaitlistEntryEntity entry, PromotionAuditEntity.PromotionAction action, 
                                    String reason, LocalDateTime actionTime) {
        PromotionAuditEntity audit = PromotionAuditEntity.builder()
            .waitlistEntry(entry)
            .user(entry.getUser())
            .classSession(entry.getClassSession())
            .action(action)
            .previousPosition(entry.getPosition())
            .promotedAt(entry.getPromotedAt())
            .holdUntil(entry.getHoldUntil())
            .confirmedAt(actionTime)
            .reason(reason)
            .createdAt(LocalDateTime.now())
            .build();

        promotionAuditRepository.save(audit);
    }

    private void publishWaitlistPromotedEvent(WaitlistEntryEntity entry) {
        UserEntity user = entry.getUser();
        ClassSessionEntity classSession = entry.getClassSession();
        
        long minutesToConfirm = java.time.Duration.between(
            LocalDateTime.now(), entry.getHoldUntil()).toMinutes();

        WaitlistPromotedEvent event = WaitlistPromotedEvent.builder()
            .waitlistEntryId(entry.getId())
            .userId(user.getId())
            .classSessionId(classSession.getId())
            .className(classSession.getClassName())
            .classType(classSession.getClassType())
            .classStartsAt(classSession.getStartsAt())
            .promotedAt(entry.getPromotedAt())
            .holdUntil(entry.getHoldUntil())
            .position(entry.getPosition())
            .userEmail(user.getEmail())
            .userFirstName(user.getFirstName())
            .userLastName(user.getLastName())
            .confirmationUrl(buildConfirmationUrl(entry.getId()))
            .minutesToConfirm(minutesToConfirm)
            .trainerName(classSession.getTrainer() != null ? 
                classSession.getTrainer().getFirstName() + " " + classSession.getTrainer().getLastName() : null)
            .build();

        eventPublisher.publishEvent(event);
    }

    private void publishWaitlistHoldExpiredEvent(WaitlistEntryEntity entry) {
        UserEntity user = entry.getUser();
        ClassSessionEntity classSession = entry.getClassSession();

        WaitlistHoldExpiredEvent event = WaitlistHoldExpiredEvent.builder()
            .waitlistEntryId(entry.getId())
            .userId(user.getId())
            .classSessionId(classSession.getId())
            .className(classSession.getClassName())
            .classStartsAt(classSession.getStartsAt())
            .expiredAt(LocalDateTime.now())
            .previousPosition(entry.getPosition())
            .userEmail(user.getEmail())
            .userFirstName(user.getFirstName())
            .userLastName(user.getLastName())
            .build();

        eventPublisher.publishEvent(event);
    }

    private void publishWaitlistConfirmedEvent(WaitlistEntryEntity entry, BookingEntity booking) {
        UserEntity user = entry.getUser();
        ClassSessionEntity classSession = entry.getClassSession();

        WaitlistConfirmedEvent event = WaitlistConfirmedEvent.builder()
            .waitlistEntryId(entry.getId())
            .bookingId(booking.getId())
            .userId(user.getId())
            .classSessionId(classSession.getId())
            .className(classSession.getClassName())
            .classStartsAt(classSession.getStartsAt())
            .confirmedAt(LocalDateTime.now())
            .previousPosition(entry.getPosition())
            .userEmail(user.getEmail())
            .userFirstName(user.getFirstName())
            .userLastName(user.getLastName())
            .trainerName(classSession.getTrainer() != null ? 
                classSession.getTrainer().getFirstName() + " " + classSession.getTrainer().getLastName() : null)
            .build();

        eventPublisher.publishEvent(event);
    }

    private String buildConfirmationUrl(Long entryId) {
        // This should be configured in properties, but for now return a template
        return "/waitlist/confirm/" + entryId;
    }
}
