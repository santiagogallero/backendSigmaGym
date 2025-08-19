package com.sigma.gym.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnfreezeResponseDTO {
    private String status;
    private String nextBillingDate;
}
