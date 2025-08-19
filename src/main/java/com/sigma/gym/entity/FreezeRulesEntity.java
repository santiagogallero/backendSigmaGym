package com.sigma.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "freeze_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreezeRulesEntity {
    
    @Id
    private Long id = 1L; // Solo habrá un registro de configuración
    
    @Column(name = "min_days", nullable = false)
    @Min(value = 1, message = "Mínimo de días debe ser mayor a 0")
    @NotNull(message = "Mínimo de días es obligatorio")
    private Integer minDays;
    
    @Column(name = "max_days", nullable = false)
    @Min(value = 1, message = "Máximo de días debe ser mayor a 0")
    @NotNull(message = "Máximo de días es obligatorio")
    private Integer maxDays;
    
    @Column(name = "max_freezes_per_year", nullable = false)
    @Min(value = 0, message = "Máximo de congelamientos por año no puede ser negativo")
    @NotNull(message = "Máximo de congelamientos por año es obligatorio")
    private Integer maxFreezesPerYear;
    
    @Column(name = "advance_notice_days", nullable = false)
    @Min(value = 0, message = "Días de aviso previo no puede ser negativo")
    @NotNull(message = "Días de aviso previo es obligatorio")
    private Integer advanceNoticeDays;
}
