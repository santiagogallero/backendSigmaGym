package com.sigma.gym.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RescheduleBookingRequestDTO {
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
    
    @NotNull(message = "New class session ID is required")
    private Long newClassSessionId;
    
    @Size(max = 500, message = "Reschedule reason cannot exceed 500 characters")
    private String reason;
}
