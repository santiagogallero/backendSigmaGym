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
    private Integer bodyFatPercentage;
    private Integer muscleMass;
    private String notes; // ✅ mejor usar String que Object para notas, así es más flexible
    
}
