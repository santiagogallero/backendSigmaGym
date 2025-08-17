package com.sigma.gym.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;

/**
 * DTO for PlanDay operations
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDayDTO {
    
    private Long id;
    
    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;
    
    @Min(value = 0, message = "Order index must be non-negative")
    private Integer orderIndex;
    
    // For creation, this will be set by the service
    private Long planId;
}
