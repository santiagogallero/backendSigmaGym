package com.sigma.gym.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinWaitlistResponseDTO {
    private boolean success;
    private String message;
    private WaitlistEntryDTO entry;
    private int queueSize;
    private String errorCode;
    
    // Class information
    private boolean waitlistEligible;
    private int availableSpots;
    private int totalCapacity;
    
    public static JoinWaitlistResponseDTO success(WaitlistEntryDTO entry, int queueSize) {
        return JoinWaitlistResponseDTO.builder()
            .success(true)
            .message("Successfully joined waitlist")
            .entry(entry)
            .queueSize(queueSize)
            .waitlistEligible(true)
            .build();
    }
    
    public static JoinWaitlistResponseDTO error(String message, String errorCode) {
        return JoinWaitlistResponseDTO.builder()
            .success(false)
            .message(message)
            .errorCode(errorCode)
            .build();
    }
    
    public static JoinWaitlistResponseDTO classFull(int queueSize, int availableSpots, int totalCapacity) {
        return JoinWaitlistResponseDTO.builder()
            .success(false)
            .message("Class is full, but you can join the waitlist")
            .errorCode("CLASS_FULL")
            .waitlistEligible(true)
            .queueSize(queueSize)
            .availableSpots(availableSpots)
            .totalCapacity(totalCapacity)
            .build();
    }
}
