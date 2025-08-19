package com.sigma.gym.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlanDTO {
    
    private Long id;
    
    @NotBlank(message = "Nombre es obligatorio")
    private String name;
    
    private String description;
    
    @NotNull(message = "Precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;
    
    @NotBlank(message = "Moneda es obligatoria")
    private String currency;
    
    @NotNull(message = "Duración es obligatoria")
    private Integer durationDays;
    
    private Boolean active;
}
