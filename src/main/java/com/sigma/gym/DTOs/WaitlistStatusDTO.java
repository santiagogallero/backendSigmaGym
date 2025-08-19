package com.sigma.gym.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitlistStatusDTO {
    private Long classSessionId;
    private String className;
    private int totalCapacity;
    private int bookedCount;
    private int availableSpots;
    private int queueSize;
    private boolean isWaitlistOpen;
    private List<WaitlistEntryDTO> entries;
    
    // For user-specific responses
    private WaitlistEntryDTO userEntry;
    private boolean userInWaitlist;
    private boolean userHasBooking;
}
