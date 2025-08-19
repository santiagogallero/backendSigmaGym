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
public class WaitlistConfirmedEvent {
    private Long waitlistEntryId;
    private Long bookingId;
    private Long userId;
    private Long classSessionId;
    private String className;
    private LocalDateTime classStartsAt;
    private LocalDateTime confirmedAt;
    private Integer previousPosition;
    private String userEmail;
    private String userFirstName;
    private String userLastName;
    private String trainerName;
}
