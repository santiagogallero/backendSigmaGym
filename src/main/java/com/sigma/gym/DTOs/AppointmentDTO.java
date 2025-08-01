package com.sigma.gym.DTOs;

import com.sigma.gym.model.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDTO {
    private Long id;
    private Long userId;
    private Long trainerId;
    private LocalDateTime date;
    private AppointmentStatus status;
}
