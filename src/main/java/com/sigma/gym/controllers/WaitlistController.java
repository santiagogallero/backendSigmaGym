package com.sigma.gym.controllers;

import com.sigma.gym.DTOs.*;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.exceptions.WaitlistException;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.services.WaitlistService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/waitlist")
@RequiredArgsConstructor
public class WaitlistController {

    private final WaitlistService waitlistService;
    private final UserRepository userRepository;

    /**
     * Join waitlist for a class session
     */
    @PostMapping("/classes/{classSessionId}/join")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<JoinWaitlistResponseDTO> joinWaitlist(
            @PathVariable Long classSessionId,
            @Valid @RequestBody(required = false) JoinWaitlistRequestDTO request,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        String note = request != null ? request.getNote() : null;
        
        log.info("User {} attempting to join waitlist for class {}", userId, classSessionId);

        try {
            JoinWaitlistResponseDTO response = waitlistService.joinWaitlist(classSessionId, userId, note);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                HttpStatus status = getHttpStatusForErrorCode(response.getErrorCode());
                return ResponseEntity.status(status).body(response);
            }
        } catch (WaitlistException e) {
            log.warn("Waitlist join failed for user {} and class {}: {}", userId, classSessionId, e.getMessage());
            JoinWaitlistResponseDTO errorResponse = JoinWaitlistResponseDTO.error(e.getMessage(), e.getErrorCode());
            HttpStatus status = getHttpStatusForErrorCode(e.getErrorCode());
            return ResponseEntity.status(status).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error joining waitlist for user {} and class {}", userId, classSessionId, e);
            JoinWaitlistResponseDTO errorResponse = JoinWaitlistResponseDTO.error(
                "An unexpected error occurred", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Leave waitlist
     */
    @PostMapping("/entries/{entryId}/leave")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> leaveWaitlist(
            @PathVariable Long entryId,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        log.info("User {} attempting to leave waitlist entry {}", userId, entryId);

        try {
            waitlistService.leaveWaitlist(entryId, userId);
            return ResponseEntity.ok().build();
        } catch (WaitlistException e) {
            log.warn("Leave waitlist failed for user {} and entry {}: {}", userId, entryId, e.getMessage());
            HttpStatus status = getHttpStatusForErrorCode(e.getErrorCode());
            return ResponseEntity.status(status).build();
        } catch (Exception e) {
            log.error("Unexpected error leaving waitlist for user {} and entry {}", userId, entryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Confirm promotion (accept spot from waitlist)
     */
    @PostMapping("/entries/{entryId}/confirm")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> confirmPromotion(
            @PathVariable Long entryId,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        log.info("User {} attempting to confirm promotion for entry {}", userId, entryId);

        try {
            BookingDTO booking = waitlistService.confirmPromotion(entryId, userId);
            return ResponseEntity.ok(booking);
        } catch (WaitlistException e) {
            log.warn("Confirm promotion failed for user {} and entry {}: {}", userId, entryId, e.getMessage());
            HttpStatus status = getHttpStatusForErrorCode(e.getErrorCode());
            return ResponseEntity.status(status).body(new ErrorResponse(e.getMessage(), e.getErrorCode()));
        } catch (Exception e) {
            log.error("Unexpected error confirming promotion for user {} and entry {}", userId, entryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("An unexpected error occurred", "INTERNAL_ERROR"));
        }
    }

    /**
     * Get user's waitlist entries
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<WaitlistEntryDTO>> getMyWaitlistEntries(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        log.debug("User {} requesting their waitlist entries", userId);

        List<WaitlistEntryDTO> entries = waitlistService.getUserWaitlistEntries(userId);
        return ResponseEntity.ok(entries);
    }

    /**
     * Get user's active waitlist entries only
     */
    @GetMapping("/me/active")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<WaitlistEntryDTO>> getMyActiveWaitlistEntries(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        log.debug("User {} requesting their active waitlist entries", userId);

        List<WaitlistEntryDTO> entries = waitlistService.getUserActiveWaitlistEntries(userId);
        return ResponseEntity.ok(entries);
    }

    /**
     * Get waitlist status for a class (admin/trainer view)
     */
    @GetMapping("/classes/{classSessionId}/waitlist")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF') or hasRole('ROLE_TRAINER')")
    public ResponseEntity<WaitlistStatusDTO> getClassWaitlist(
            @PathVariable Long classSessionId,
            Authentication authentication) {

        Long currentUserId = getCurrentUserId(authentication);
        log.debug("Admin/Trainer {} requesting waitlist status for class {}", currentUserId, classSessionId);

        WaitlistStatusDTO status = waitlistService.getWaitlistStatus(classSessionId, null);
        return ResponseEntity.ok(status);
    }

    /**
     * Get waitlist status for a class (user view - includes their position)
     */
    @GetMapping("/classes/{classSessionId}/status")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<WaitlistStatusDTO> getClassWaitlistStatus(
            @PathVariable Long classSessionId,
            Authentication authentication) {

        Long userId = getCurrentUserId(authentication);
        log.debug("User {} requesting waitlist status for class {}", userId, classSessionId);

        WaitlistStatusDTO status = waitlistService.getWaitlistStatus(classSessionId, userId);
        return ResponseEntity.ok(status);
    }

    /**
     * Manual promotion trigger (admin only)
     */
    @PostMapping("/classes/{classSessionId}/promote-next")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> promoteNext(
            @PathVariable Long classSessionId,
            Authentication authentication) {

        Long adminId = getCurrentUserId(authentication);
        log.info("Admin {} manually triggering promotion for class {}", adminId, classSessionId);

        try {
            waitlistService.promoteNext(classSessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error in manual promotion for class {}", classSessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Process expired holds manually (admin only)
     */
    @PostMapping("/admin/process-expired-holds")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> processExpiredHolds(Authentication authentication) {

        Long adminId = getCurrentUserId(authentication);
        log.info("Admin {} manually triggering expired holds processing", adminId);

        try {
            waitlistService.processExpiredHolds();
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing expired holds", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return user.getId();
    }

    private HttpStatus getHttpStatusForErrorCode(String errorCode) {
        return switch (errorCode) {
            case "CLASS_FULL", "ALREADY_IN_WAITLIST", "ALREADY_BOOKED", "MEMBERSHIP_FROZEN", "WAITLIST_FULL" -> 
                HttpStatus.CONFLICT;
            case "CUTOFF_REACHED", "CLASS_HAS_CAPACITY", "INVALID_STATUS" -> 
                HttpStatus.BAD_REQUEST;
            case "HOLD_EXPIRED" -> 
                HttpStatus.GONE;
            case "ENTRY_NOT_FOUND" -> 
                HttpStatus.NOT_FOUND;
            case "UNAUTHORIZED" -> 
                HttpStatus.FORBIDDEN;
            case "NO_ACTIVE_MEMBERSHIP" -> 
                HttpStatus.PAYMENT_REQUIRED;
            default -> 
                HttpStatus.BAD_REQUEST;
        };
    }

    // Simple error response class
    @Getter
    private static class ErrorResponse {
        public final String message;
        public final String errorCode;

        public ErrorResponse(String message, String errorCode) {
            this.message = message;
            this.errorCode = errorCode;
        }
    }
}
