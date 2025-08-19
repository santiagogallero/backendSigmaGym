package com.sigma.gym.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaitlistHoldExpiredEvent {
    private Long waitlistEntryId;
    private Long userId;
    private Long classSessionId;
    private String className;
    private LocalDateTime classStartsAt;
    private LocalDateTime expiredAt;
    private Integer previousPosition;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    
    // Context for next promotion
    private boolean nextUserPromoted;
    private Long nextUserId;
    private String nextUserEmail;
}
