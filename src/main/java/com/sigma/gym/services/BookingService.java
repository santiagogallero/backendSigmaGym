package com.sigma.gym.services;

import com.sigma.gym.DTOs.BookingActionResponseDTO;
import com.sigma.gym.DTOs.BookingDTO;
import com.sigma.gym.config.BookingProperties;
import com.sigma.gym.entity.*;
import com.sigma.gym.exceptions.*;
import com.sigma.gym.repository.BookingAuditRepository;
import com.sigma.gym.repository.BookingRepository;
import com.sigma.gym.repository.ClassSessionRepository;
import com.sigma.gym.repository.MembershipRepository;
import com.sigma.gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    
    private final BookingRepository bookingRepository;
    private final ClassSessionRepository classSessionRepository;
    private final BookingAuditRepository bookingAuditRepository;
    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final BookingProperties bookingProperties;
    private final WaitlistService waitlistService;

    /**
     * Cancel a booking with policy validation and audit logging
     */
    @Transactional
    public BookingActionResponseDTO cancelBooking(Long bookingId, Long userId, String reason) {
        try {
            log.info("Attempting to cancel booking {} for user {}", bookingId, userId);
            
            // Get booking with pessimistic lock
            BookingEntity booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
            
            // Validate ownership (unless admin)
            UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
            
            validateBookingOwnership(booking, userId, user);
            
            // Validate cancellation is allowed
            validateCancellation(booking, user);
            
            // Check monthly limits
            validateMonthlyCancellationLimit(userId, booking.getClassSession().getStartsAt());
            
            // Update booking status
            booking.setStatus(BookingStatus.CANCELLED);
            booking.setCancelledAt(LocalDateTime.now());
            final BookingEntity finalBooking = bookingRepository.save(booking);
            
            // Update class session capacity
            ClassSessionEntity classSession = classSessionRepository.findByIdForUpdate(finalBooking.getClassSessionId())
                .orElseThrow(() -> new ClassSessionNotFoundException(finalBooking.getClassSessionId()));
            classSession.decrementBookedCount();
            classSessionRepository.save(classSession);
            
            // Create audit record
            createAuditRecord(finalBooking, user, user, BookingAuditEntity.BookingAction.CANCELLED, reason, null);
            
            // Try to promote next waitlist user
            waitlistService.promoteNext(finalBooking.getClassSessionId());
            
            log.info("Successfully cancelled booking {} for user {}", bookingId, userId);
            
            return BookingActionResponseDTO.success(
                "Booking cancelled successfully", 
                BookingDTO.fromEntity(finalBooking)
            );
            
        } catch (BookingPolicyViolationException e) {
            log.warn("Policy violation while cancelling booking {}: {}", bookingId, e.getMessage());
            return BookingActionResponseDTO.error(e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            log.error("Error cancelling booking {}: {}", bookingId, e.getMessage(), e);
            return BookingActionResponseDTO.error("Failed to cancel booking", "INTERNAL_ERROR");
        }
    }

    /**
     * Reschedule a booking to a new class session
     */
    @Transactional
    public BookingActionResponseDTO rescheduleBooking(Long bookingId, Long newClassSessionId, Long userId, String reason) {
        try {
            log.info("Attempting to reschedule booking {} to session {} for user {}", 
                bookingId, newClassSessionId, userId);
            
            // Get original booking with pessimistic lock
            BookingEntity originalBooking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
            
            // Validate ownership
            UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
            
            validateBookingOwnership(originalBooking, userId, user);
            
            // Validate reschedule is allowed
            validateReschedule(originalBooking, user);
            
            // Check monthly limits
            validateMonthlyRescheduleLimit(userId, originalBooking.getClassSession().getStartsAt());
            
            // Get new class session with lock
            ClassSessionEntity newClassSession = classSessionRepository.findByIdForUpdate(newClassSessionId)
                .orElseThrow(() -> new ClassSessionNotFoundException(newClassSessionId));
            
            // Validate new session capacity
            if (newClassSession.isFull()) {
                throw new ClassFullException("New class session is full");
            }
            
            // Validate membership allows new session
            validateMembershipForSession(user, newClassSession);
            
            // Update original booking
            originalBooking.setStatus(BookingStatus.REPROGRAMMED);
            originalBooking.setRescheduledAt(LocalDateTime.now());
            final BookingEntity finalOriginalBooking = bookingRepository.save(originalBooking);
            
            // Create new booking
            BookingEntity newBooking = BookingEntity.builder()
                .user(user)
                .classSession(newClassSession)
                .status(BookingStatus.CONFIRMED)
                .rescheduledFromBookingId(finalOriginalBooking.getId())
                .createdAt(LocalDateTime.now())
                .build();
            newBooking = bookingRepository.save(newBooking);
            
            // Update class session capacities
            ClassSessionEntity originalSession = classSessionRepository.findByIdForUpdate(finalOriginalBooking.getClassSessionId())
                .orElseThrow(() -> new ClassSessionNotFoundException(finalOriginalBooking.getClassSessionId()));
            originalSession.decrementBookedCount();
            classSessionRepository.save(originalSession);
            
            newClassSession.incrementBookedCount();
            classSessionRepository.save(newClassSession);
            
            // Create audit records
            createAuditRecord(finalOriginalBooking, user, user, BookingAuditEntity.BookingAction.RESCHEDULED, reason, 
                String.format("{\"newBookingId\":%d,\"newSessionId\":%d}", newBooking.getId(), newClassSessionId));
            createAuditRecord(newBooking, user, user, BookingAuditEntity.BookingAction.CREATED, 
                "Rescheduled from booking " + finalOriginalBooking.getId(), 
                String.format("{\"originalBookingId\":%d}", finalOriginalBooking.getId()));
                
            // Try to promote waitlist user for the original session
            waitlistService.promoteNext(finalOriginalBooking.getClassSessionId());
            
            log.info("Successfully rescheduled booking {} to new booking {} for user {}", 
                bookingId, newBooking.getId(), userId);
            
            return BookingActionResponseDTO.success(
                "Booking rescheduled successfully", 
                BookingDTO.fromEntity(newBooking)
            );
            
        } catch (BookingPolicyViolationException | ClassFullException e) {
            log.warn("Validation error while rescheduling booking {}: {}", bookingId, e.getMessage());
            return BookingActionResponseDTO.error(e.getMessage(), "RESCHEDULE_FAILED");
        } catch (Exception e) {
            log.error("Error rescheduling booking {}: {}", bookingId, e.getMessage(), e);
            return BookingActionResponseDTO.error("Failed to reschedule booking", "INTERNAL_ERROR");
        }
    }

    /**
     * Get user's booking history with audit trail
     */
    @Transactional(readOnly = true)
    public List<BookingDTO> getUserBookings(Long userId) {
        List<BookingEntity> bookings = bookingRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return bookings.stream()
            .map(BookingDTO::fromEntity)
            .toList();
    }

    /**
     * Get booking by ID with ownership validation
     */
    @Transactional(readOnly = true)
    public Optional<BookingDTO> getBooking(Long bookingId, Long userId) {
        return bookingRepository.findById(bookingId)
            .filter(booking -> booking.getUserId().equals(userId) || isAdmin(userId))
            .map(BookingDTO::fromEntity);
    }

    // Validation methods
    private void validateBookingOwnership(BookingEntity booking, Long userId, UserEntity user) {
        if (!booking.getUserId().equals(userId) && !isAdmin(user)) {
            throw new BookingPolicyViolationException("Not authorized to modify this booking", "UNAUTHORIZED");
        }
    }

    private void validateCancellation(BookingEntity booking, UserEntity user) {
        if (!booking.canBeCancelled()) {
            throw new BookingPolicyViolationException("Booking cannot be cancelled in current status", "INVALID_STATUS");
        }
        
        if (!isAdmin(user)) {
            LocalDateTime classStart = booking.getClassSession().getStartsAt();
            LocalDateTime cutoffTime = classStart.minusHours(bookingProperties.getCancelWindowHours());
            
            if (LocalDateTime.now().isAfter(cutoffTime)) {
                throw new BookingPolicyViolationException(
                    String.format("Booking can only be cancelled up to %d hours before class start", 
                        bookingProperties.getCancelWindowHours()), 
                    "CANCEL_WINDOW_EXPIRED"
                );
            }
        }
        
        validateMembershipStatus(user);
    }

    private void validateReschedule(BookingEntity booking, UserEntity user) {
        if (!booking.canBeRescheduled()) {
            throw new BookingPolicyViolationException("Booking cannot be rescheduled in current status", "INVALID_STATUS");
        }
        
        if (!isAdmin(user)) {
            LocalDateTime classStart = booking.getClassSession().getStartsAt();
            LocalDateTime cutoffTime = classStart.minusHours(bookingProperties.getRescheduleWindowHours());
            
            if (LocalDateTime.now().isAfter(cutoffTime)) {
                throw new BookingPolicyViolationException(
                    String.format("Booking can only be rescheduled up to %d hours before class start", 
                        bookingProperties.getRescheduleWindowHours()), 
                    "RESCHEDULE_WINDOW_EXPIRED"
                );
            }
        }
        
        validateMembershipStatus(user);
    }

    private void validateMonthlyCancellationLimit(Long userId, LocalDateTime classStartTime) {
        YearMonth month = YearMonth.from(classStartTime);
        int cancellationCount = bookingAuditRepository.countCancelActionsByUserInMonth(userId, month);
        
        if (cancellationCount >= bookingProperties.getMaxCancellationsPerMonth()) {
            throw new BookingPolicyViolationException(
                String.format("Monthly cancellation limit exceeded (%d/%d)", 
                    cancellationCount, bookingProperties.getMaxCancellationsPerMonth()),
                "MONTHLY_CANCEL_LIMIT_EXCEEDED"
            );
        }
    }

    private void validateMonthlyRescheduleLimit(Long userId, LocalDateTime classStartTime) {
        YearMonth month = YearMonth.from(classStartTime);
        int rescheduleCount = bookingAuditRepository.countRescheduleActionsByUserInMonth(userId, month);
        
        if (rescheduleCount >= bookingProperties.getMaxReschedulesPerMonth()) {
            throw new BookingPolicyViolationException(
                String.format("Monthly reschedule limit exceeded (%d/%d)", 
                    rescheduleCount, bookingProperties.getMaxReschedulesPerMonth()),
                "MONTHLY_RESCHEDULE_LIMIT_EXCEEDED"
            );
        }
    }

    private void validateMembershipStatus(UserEntity user) {
        Optional<MembershipEntity> activeMembership = membershipRepository.findActiveMembershipByUserId(user.getId());
        
        if (activeMembership.isEmpty()) {
            throw new BookingPolicyViolationException("No active membership found", "NO_ACTIVE_MEMBERSHIP");
        }
        
        MembershipEntity membership = activeMembership.get();
        if (membership.getStatus() == MembershipEntity.MembershipStatus.FROZEN) {
            throw new BookingPolicyViolationException("Membership is frozen", "MEMBERSHIP_FROZEN");
        }
    }

    private void validateMembershipForSession(UserEntity user, ClassSessionEntity session) {
        validateMembershipStatus(user);
        // Additional validation could include checking if membership plan allows this class type
    }

    private boolean isAdmin(UserEntity user) {
        // Check if user has admin role - adjust based on your role system
        return user.getRoles().stream()
            .anyMatch(role -> "ADMIN".equals(role.getName()) || "STAFF".equals(role.getName()));
    }

    private boolean isAdmin(Long userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        return user != null && isAdmin(user);
    }

    private void createAuditRecord(BookingEntity booking, UserEntity user, UserEntity actor, 
                                 BookingAuditEntity.BookingAction action, String reason, String metadata) {
        BookingAuditEntity audit = BookingAuditEntity.builder()
            .booking(booking)
            .user(user)
            .actorUser(actor)
            .action(action)
            .reason(reason)
            .metadataJson(metadata)
            .createdAt(LocalDateTime.now())
            .build();
        
        bookingAuditRepository.save(audit);
    }
}
