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
public class CancelBookingRequestDTO {
    @NotNull(message = "Booking ID is required")
    private Long bookingId;
    
    @Size(max = 500, message = "Cancellation reason cannot exceed 500 characters")
    private String reason;
}
