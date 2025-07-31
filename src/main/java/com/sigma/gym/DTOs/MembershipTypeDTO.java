package com.sigma.gym.DTOs;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipTypeDTO {
    private Long id;
    private String name;
    private Integer allowedDaysPerWeek; // Ej: 2, 3, 4, 7

    private Integer durationInDays; // Ej: 30 para mensual, 90 para trimestral, etc.

    private boolean isActive;
}
