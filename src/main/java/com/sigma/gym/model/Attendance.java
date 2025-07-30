package com.sigma.gym.model;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Attendance {
    private Long id;
    private Long userId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
}
