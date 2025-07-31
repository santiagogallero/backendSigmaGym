package com.sigma.gym.model;

import java.time.LocalDate;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Progress {
    private Long id;
    private Long userId;
    private LocalDate date;
    private Double weight;
    private String notes;
    private Double muscleMass;
    private Double fatPercentage;
}
