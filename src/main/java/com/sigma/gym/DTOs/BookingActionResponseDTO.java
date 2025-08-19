package com.sigma.gym.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingActionResponseDTO {
    private boolean success;
    private String message;
    private BookingDTO booking;
    private String errorCode;
    
    // Waitlist information for CLASS_FULL scenarios
    private boolean waitlistEligible;
    private Integer queueSize;
    private Integer availableSpots;
    private Integer totalCapacity;

    public static BookingActionResponseDTO success(String message, BookingDTO booking) {
        return BookingActionResponseDTO.builder()
            .success(true)
            .message(message)
            .booking(booking)
            .build();
    }

    public static BookingActionResponseDTO error(String message, String errorCode) {
        return BookingActionResponseDTO.builder()
            .success(false)
            .message(message)
            .errorCode(errorCode)
            .build();
    }
    
    public static BookingActionResponseDTO classFull(String message, Integer queueSize, 
                                                   Integer availableSpots, Integer totalCapacity) {
        return BookingActionResponseDTO.builder()
            .success(false)
            .message(message)
            .errorCode("CLASS_FULL")
            .waitlistEligible(true)
            .queueSize(queueSize)
            .availableSpots(availableSpots)
            .totalCapacity(totalCapacity)
            .build();
    }
}
