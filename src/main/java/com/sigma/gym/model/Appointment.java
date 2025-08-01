package com.sigma.gym.model;

import java.time.LocalDateTime;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    private Long id;
    private Long userId;
    private Long trainerId;
    private LocalDateTime date;
    private AppointmentStatus status;

    // Constructors, getters and setters
}
