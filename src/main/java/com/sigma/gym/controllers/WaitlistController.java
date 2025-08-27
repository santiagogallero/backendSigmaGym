package com.sigma.gym.controllers;

import com.sigma.gym.DTOs.*;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.exceptions.WaitlistException;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.services.WaitlistService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(prefix = "sigmagym.features.waitlist", name = "enabled", havingValue = "true", matchIfMissing = true)
public class WaitlistController {

    private final WaitlistService waitlistService;
    private final UserRepository userRepository;

    /**
     * Join waitlist for a class
     */
    @PostMapping("/classes/{classSessionId}/join")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_TRAINER', 'ROLE_OWNER')")
    public ResponseEntity<?> joinWaitlist(
            @PathVariable Long classSessionId,
            @RequestBody(required = false) @Valid JoinWaitlistRequestDTO request,
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
    @DeleteMapping("/entries/{entryId}")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_TRAINER', 'ROLE_OWNER')")
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
     * Confirm waitlist promotion
     */
    @PostMapping("/entries/{entryId}/confirm")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_TRAINER', 'ROLE_OWNER')")
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
            ErrorResponse errorResponse = new ErrorResponse(e.getMessage(), e.getErrorCode());
            HttpStatus status = getHttpStatusForErrorCode(e.getErrorCode());
            return ResponseEntity.status(status).body(errorResponse);
        } catch (Exception e) {
            log.error("Unexpected error confirming promotion for user {} and entry {}", userId, entryId, e);
            ErrorResponse errorResponse = new ErrorResponse("An unexpected error occurred", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get user's waitlist entries
     */
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_TRAINER', 'ROLE_OWNER')")
    public ResponseEntity<?> getMyWaitlistEntries(Authentication authentication) {
        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            ErrorResponse errorResponse = new ErrorResponse("User not authenticated", "UNAUTHORIZED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            Long userId = getCurrentUserId(authentication);
            log.debug("User {} requesting their waitlist entries", userId);

            List<WaitlistEntryDTO> entries = waitlistService.getUserWaitlistEntries(userId);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            log.error("Error retrieving waitlist entries for authenticated user", e);
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get user's active waitlist entries only
     */
    @GetMapping("/me/active")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_TRAINER', 'ROLE_OWNER')")
    public ResponseEntity<?> getMyActiveWaitlistEntries(Authentication authentication) {
        // Check if user is authenticated
        if (authentication == null || !authentication.isAuthenticated()) {
            ErrorResponse errorResponse = new ErrorResponse("User not authenticated", "UNAUTHORIZED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        try {
            Long userId = getCurrentUserId(authentication);
            log.debug("User {} requesting their active waitlist entries", userId);

            List<WaitlistEntryDTO> entries = waitlistService.getUserActiveWaitlistEntries(userId);
            return ResponseEntity.ok(entries);
        } catch (Exception e) {
            log.error("Error retrieving active waitlist entries for authenticated user", e);
            ErrorResponse errorResponse = new ErrorResponse("Internal server error", "INTERNAL_ERROR");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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
    @PreAuthorize("hasAnyRole('ROLE_MEMBER', 'ROLE_TRAINER', 'ROLE_OWNER')")
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
        
        Long adminUserId = getCurrentUserId(authentication);
        log.info("Admin {} manually promoting next user for class {}", adminUserId, classSessionId);

        try {
            waitlistService.promoteNext(classSessionId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error promoting next user for class {}", classSessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Process expired holds manually (admin only)
     */
    @PostMapping("/admin/process-expired-holds")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> processExpiredHolds(Authentication authentication) {
        Long adminUserId = getCurrentUserId(authentication);
        log.info("Admin {} manually processing expired holds", adminUserId);

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
            default -> 
                HttpStatus.INTERNAL_SERVER_ERROR;
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
