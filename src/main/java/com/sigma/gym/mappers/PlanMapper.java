package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.plans.PlanDTO;
import com.sigma.gym.DTOs.plans.PlansSettingsDTO;
import com.sigma.gym.entity.PlanEntity;
import com.sigma.gym.entity.PlanPriceTierEntity;
import com.sigma.gym.entity.PlansSettingsEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PlanMapper {
    
    public PlanDTO toDTO(PlanEntity entity) {
        if (entity == null) return null;
        
        return PlanDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .category(entity.getCategory())
                .description(entity.getDescription())
                .visible(entity.getVisible())
                .sortOrder(entity.getSortOrder())
                .validFrom(entity.getValidFrom())
                .validUntil(entity.getValidUntil())
                .flatPriceARS(entity.getFlatPriceARS())
                .metadata(entity.getMetadata())
                .priceTiers(entity.getPriceTiers() != null ? 
                    entity.getPriceTiers().stream()
                            .map(this::toPriceTierDTO)
                            .collect(Collectors.toList()) : null)
                .build();
    }
    
    public PlanEntity toEntity(PlanDTO dto) {
        if (dto == null) return null;
        
        return PlanEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .visible(dto.getVisible())
                .sortOrder(dto.getSortOrder())
                .validFrom(dto.getValidFrom())
                .validUntil(dto.getValidUntil())
                .flatPriceARS(dto.getFlatPriceARS())
                .metadata(dto.getMetadata())
                .build();
    }
    
    public PlanDTO.PlanPriceTierDTO toPriceTierDTO(PlanPriceTierEntity entity) {
        if (entity == null) return null;
        
        return PlanDTO.PlanPriceTierDTO.builder()
                .id(entity.getId())
                .timesPerWeek(entity.getTimesPerWeek())
                .priceARS(entity.getPriceARS())
                .build();
    }
    
    public PlanPriceTierEntity toPriceTierEntity(PlanDTO.PlanPriceTierDTO dto, PlanEntity plan) {
        if (dto == null) return null;
        
        return PlanPriceTierEntity.builder()
                .id(dto.getId())
                .timesPerWeek(dto.getTimesPerWeek())
                .priceARS(dto.getPriceARS())
                .plan(plan)
                .build();
    }
    
    @SuppressWarnings("unchecked")
    public PlansSettingsDTO toSettingsDTO(PlansSettingsEntity entity) {
        if (entity == null) return null;
        
        List<PlansSettingsDTO.DiscountRule> discountRules = null;
        if (entity.getDiscountsJson() != null && entity.getDiscountsJson().containsKey("rules")) {
            try {
                List<Map<String, Object>> rulesMap = (List<Map<String, Object>>) entity.getDiscountsJson().get("rules");
                discountRules = rulesMap.stream()
                        .map(this::mapToDiscountRule)
                        .collect(Collectors.toList());
            } catch (Exception e) {
                // Handle malformed JSON gracefully
                discountRules = List.of();
            }
        }
        
        return PlansSettingsDTO.builder()
                .discountRules(discountRules != null ? discountRules : List.of())
                .payment(PlansSettingsDTO.PaymentInfo.builder()
                        .cbuCvu(entity.getPaymentCbuCvu())
                        .alias(entity.getPaymentAlias())
                        .whatsapp(entity.getPaymentWhatsapp())
                        .showOnPage(entity.getPaymentShowOnPage())
                        .notes(entity.getPaymentNotes())
                        .build())
                .build();
    }
    
    public PlansSettingsEntity toSettingsEntity(PlansSettingsDTO dto) {
        if (dto == null) return null;
        
        Map<String, Object> discountsJson = null;
        if (dto.getDiscountRules() != null) {
            List<Map<String, Object>> rules = dto.getDiscountRules().stream()
                    .map(this::discountRuleToMap)
                    .collect(Collectors.toList());
            discountsJson = Map.of("rules", rules);
        }
        
        PlansSettingsDTO.PaymentInfo payment = dto.getPayment();
        
        return PlansSettingsEntity.builder()
                .id(1L)
                .paymentCbuCvu(payment != null ? payment.getCbuCvu() : null)
                .paymentAlias(payment != null ? payment.getAlias() : null)
                .paymentWhatsapp(payment != null ? payment.getWhatsapp() : null)
                .paymentShowOnPage(payment != null ? payment.getShowOnPage() : true)
                .paymentNotes(payment != null ? payment.getNotes() : null)
                .discountsJson(discountsJson)
                .build();
    }
    
    @SuppressWarnings("unchecked")
    private PlansSettingsDTO.DiscountRule mapToDiscountRule(Map<String, Object> map) {
        return PlansSettingsDTO.DiscountRule.builder()
                .type((String) map.get("type"))
                .description((String) map.get("description"))
                .percentage(map.get("percentage") instanceof Number ? 
                    ((Number) map.get("percentage")).doubleValue() : null)
                .conditions((Map<String, Object>) map.get("conditions"))
                .build();
    }
    
    private Map<String, Object> discountRuleToMap(PlansSettingsDTO.DiscountRule rule) {
        return Map.of(
                "type", rule.getType() != null ? rule.getType() : "",
                "description", rule.getDescription() != null ? rule.getDescription() : "",
                "percentage", rule.getPercentage() != null ? rule.getPercentage() : 0.0,
                "conditions", rule.getConditions() != null ? rule.getConditions() : Map.of()
        );
    }
}
