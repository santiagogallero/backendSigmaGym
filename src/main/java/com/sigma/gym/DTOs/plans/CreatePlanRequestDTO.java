package com.sigma.gym.DTOs.plans;

import com.sigma.gym.DTOs.plans.PlanDTO.PlanPriceTierDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePlanRequestDTO {
    
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;
    
    @NotBlank(message = "Category is required")
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;
    
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
    
    @NotNull(message = "Visible flag is required")
    private Boolean visible;
    
    @NotNull(message = "Sort order is required")
    @Min(value = 0, message = "Sort order must be 0 or greater")
    private Integer sortOrder;
    
    private LocalDate validFrom;
    
    private LocalDate validUntil;
    
    @DecimalMin(value = "0", message = "Flat price must be 0 or greater")
    @Digits(integer = 10, fraction = 2, message = "Invalid price format")
    private BigDecimal flatPriceARS;
    
    private Map<String, Object> metadata;
    
    @Valid
    private List<PlanPriceTierDTO> priceTiers;
}
