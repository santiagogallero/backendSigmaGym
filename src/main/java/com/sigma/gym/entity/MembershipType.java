package com.sigma.gym.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembershipType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // Ej: "2 veces por semana", "Ilimitado", "Mensual", etc.

    private Integer allowedDaysPerWeek; // Ej: 2, 3, 4, 7

    private Integer durationInDays; // Ej: 30 para mensual, 90 para trimestral, etc.

    private boolean isActive;
}
