package com.sigma.gym.DTOs.plans;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDTO {
    
    private Long id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private String description;
    
    @NotNull
    private Boolean visible;
    
    @NotNull
    private Integer sortOrder;
    
    private LocalDate validFrom;
    
    private LocalDate validUntil;
    
    @PositiveOrZero(message = "Flat price must be zero or positive")
    private BigDecimal flatPriceARS;
    
    private Map<String, Object> metadata;
    
    private List<PlanPriceTierDTO> priceTiers;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanPriceTierDTO {
        private Long id;
        
        @NotNull
        private Integer timesPerWeek;
        
        @NotNull
        @PositiveOrZero(message = "Price must be zero or positive")
        private BigDecimal priceARS;
    }
}
