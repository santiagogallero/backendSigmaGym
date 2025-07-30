package com.sigma.gym.DTOs;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProgressDTO {
    private Long id;
    private Long userId;
    private LocalDate date; // ✅ mejor usar LocalDate que String si podés
    private Double weight;
    private Double bodyFatPercentage;
    private Integer muscleMass;
    private Integer waterPercentage;
}
