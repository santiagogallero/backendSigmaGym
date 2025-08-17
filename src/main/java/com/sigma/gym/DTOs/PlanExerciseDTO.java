package com.sigma.gym.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

/**
 * DTO for PlanExercise operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanExerciseDTO {
    
    private Long id;
    
    @NotBlank(message = "Exercise name is required")
    @Size(max = 100, message = "Exercise name must not exceed 100 characters")
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Min(value = 1, message = "Reps must be at least 1")
    private Integer reps;
    
    @Min(value = 1, message = "Sets must be at least 1")
    private Integer sets;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "Weight must be non-negative")
    private BigDecimal weight;
    
    @Size(max = 10, message = "Weight unit must not exceed 10 characters")
    private String weightUnit;
    
    @Min(value = 0, message = "Rest time must be non-negative")
    private Integer restTimeSeconds;
    
    @Size(max = 1000, message = "Notes must not exceed 1000 characters")
    private String notes;
    
    @Builder.Default
    private Boolean isWarmup = false;
    
    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;
    
    // For creation, this will be set by the service
    private Long dayId;
}
