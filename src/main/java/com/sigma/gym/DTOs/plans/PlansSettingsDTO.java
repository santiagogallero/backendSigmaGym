package com.sigma.gym.DTOs.plans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlansSettingsDTO {
    
    private List<DiscountRule> discountRules;
    
    private PaymentInfo payment;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DiscountRule {
        private String type;
        private String description;
        private Double percentage;
        private Map<String, Object> conditions;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfo {
        private String cbuCvu;
        private String alias;
        private String whatsapp;
        private Boolean showOnPage;
        private String notes;
    }
}
