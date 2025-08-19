package com.sigma.gym.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreezeResponseDTO {
    private String status;
    private FreezeDTO freeze;
    private String nextBillingDate;
}
