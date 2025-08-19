package com.sigma.gym.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipPlanEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "El nombre del plan es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String name;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;
    
    @Column(name = "currency", nullable = false, length = 3)
    @NotBlank(message = "La moneda es obligatoria")
    @Size(max = 3, message = "La moneda debe tener máximo 3 caracteres")
    @Builder.Default
    private String currency = "ARS";
    
    @Column(name = "description", length = 500)
    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    @Column(name = "duration_days", nullable = false)
    @NotNull(message = "La duración en días es obligatoria")
    @Builder.Default
    private Integer durationDays = 30;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
