package com.sigma.gym.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FreezeRulesDTO {
    private Integer minDays;
    private Integer maxDays;
    private Integer maxFreezesPerYear;
    private Integer advanceNoticeDays;
    private Integer remainingFreezes;
}
