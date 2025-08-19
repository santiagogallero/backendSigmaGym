package com.sigma.gym.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePreferenceRequestDTO {
    
    @NotNull(message = "Plan ID is required")
    @Positive(message = "Plan ID must be a positive number")
    private Long planId;
}
