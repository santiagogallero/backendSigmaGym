package com.sigma.gym.DTOs;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceDTO {
    private Long id;
    private LocalDate date;
    private String status; // "CHECKED_IN", "CHECKED_OUT", "ABSENT"
    private Long userId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    
}
