package com.sigma.gym.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipStatusContractDTO {
    private String status;
    private PlanSummaryDTO plan;
    private String nextBillingDate;
    private FreezeWindowDTO freezeWindow;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanSummaryDTO {
        private Long id;
        private String name;
        private Double price;
        private String currency;
    }
}
