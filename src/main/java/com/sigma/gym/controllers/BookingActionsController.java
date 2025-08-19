package com.sigma.gym.controllers;

import com.sigma.gym.DTOs.*;
import com.sigma.gym.services.BookingService;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v2/bookings")
@RequiredArgsConstructor
public class BookingActionsController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    /**
     * Cancel a booking
     */
    @PostMapping("/{bookingId}/cancel")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<BookingActionResponseDTO> cancelBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody CancelBookingRequestDTO request,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        log.info("User {} attempting to cancel booking {}", userId, bookingId);
        
        // Validate booking ID matches the one in the request
        if (!bookingId.equals(request.getBookingId())) {
            return ResponseEntity.badRequest().body(
                BookingActionResponseDTO.error("Booking ID mismatch", "INVALID_REQUEST")
            );
        }
        
        BookingActionResponseDTO response = bookingService.cancelBooking(
            bookingId, userId, request.getReason()
        );
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Reschedule a booking
     */
    @PostMapping("/{bookingId}/reschedule")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<BookingActionResponseDTO> rescheduleBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody RescheduleBookingRequestDTO request,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        log.info("User {} attempting to reschedule booking {} to session {}", 
            userId, bookingId, request.getNewClassSessionId());
        
        // Validate booking ID matches the one in the request
        if (!bookingId.equals(request.getBookingId())) {
            return ResponseEntity.badRequest().body(
                BookingActionResponseDTO.error("Booking ID mismatch", "INVALID_REQUEST")
            );
        }
        
        BookingActionResponseDTO response = bookingService.rescheduleBooking(
            bookingId, request.getNewClassSessionId(), userId, request.getReason()
        );
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get user's booking history
     */
    @GetMapping("/my-bookings")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<BookingDTO>> getMyBookings(Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        log.debug("User {} requesting booking history", userId);
        
        List<BookingDTO> bookings = bookingService.getUserBookings(userId);
        return ResponseEntity.ok(bookings);
    }

    /**
     * Get specific booking details
     */
    @GetMapping("/{bookingId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<BookingDTO> getBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {
        
        Long userId = getCurrentUserId(authentication);
        log.debug("User {} requesting booking details for {}", userId, bookingId);
        
        Optional<BookingDTO> booking = bookingService.getBooking(bookingId, userId);
        
        if (booking.isPresent()) {
            return ResponseEntity.ok(booking.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Admin endpoint: Cancel any booking
     */
    @PostMapping("/{bookingId}/admin-cancel")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public ResponseEntity<BookingActionResponseDTO> adminCancelBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody CancelBookingRequestDTO request,
            Authentication authentication) {
        
        Long adminId = getCurrentUserId(authentication);
        log.info("Admin {} cancelling booking {} for user", adminId, bookingId);
        
        // For admin cancellation, we use the booking's user ID, not the admin's
        // The service will handle admin authorization checks
        BookingActionResponseDTO response = bookingService.cancelBooking(
            bookingId, adminId, "Admin cancellation: " + request.getReason()
        );
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Admin endpoint: Reschedule any booking
     */
    @PostMapping("/{bookingId}/admin-reschedule")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STAFF')")
    public ResponseEntity<BookingActionResponseDTO> adminRescheduleBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody RescheduleBookingRequestDTO request,
            Authentication authentication) {
        
        Long adminId = getCurrentUserId(authentication);
        log.info("Admin {} rescheduling booking {} to session {}", 
            adminId, bookingId, request.getNewClassSessionId());
        
        BookingActionResponseDTO response = bookingService.rescheduleBooking(
            bookingId, request.getNewClassSessionId(), adminId, 
            "Admin reschedule: " + request.getReason()
        );
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get current user ID from Authentication
     */
    private Long getCurrentUserId(Authentication authentication) {
        String email = authentication.getName();
        UserEntity user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return user.getId();
    }
}
