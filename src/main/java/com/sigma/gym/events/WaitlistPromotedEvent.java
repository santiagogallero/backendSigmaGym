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
public class WaitlistPromotedEvent {
    private Long waitlistEntryId;
    private Long userId;
    private Long classSessionId;
    private String className;
    private String classType;
    private LocalDateTime classStartsAt;
    private LocalDateTime promotedAt;
    private LocalDateTime holdUntil;
    private Integer position;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    
    // Notification context
    private String confirmationUrl;
    private long minutesToConfirm;
    private String trainerName;
}
