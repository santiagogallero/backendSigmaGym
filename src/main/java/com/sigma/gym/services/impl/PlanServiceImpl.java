package com.sigma.gym.services.impl;

import com.sigma.gym.DTOs.plans.CreatePlanRequestDTO;
import com.sigma.gym.DTOs.plans.PlanDTO;
import com.sigma.gym.DTOs.plans.UpdatePlanRequestDTO;
import com.sigma.gym.entity.PlanEntity;
import com.sigma.gym.entity.PlanPriceTierEntity;
import com.sigma.gym.mappers.PlanMapper;
import com.sigma.gym.repository.PlanPriceTierRepository;
import com.sigma.gym.repository.PlanRepository;
import com.sigma.gym.services.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    
    private final PlanRepository planRepository;
    private final PlanPriceTierRepository priceTierRepository;
    private final PlanMapper planMapper;
    
    @Override
    public List<PlanDTO> getVisiblePlans() {
        return planRepository.findByVisibleTrueOrderBySortOrderAsc()
                .stream()
                .map(planMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PlanDTO> getAllPlans() {
        return planRepository.findAllByOrderBySortOrderAsc()
                .stream()
                .map(planMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public PlanDTO getPlanById(Long id) {
        PlanEntity plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + id));
        return planMapper.toDTO(plan);
    }
    
    @Override
    @Transactional
    public PlanDTO createPlan(CreatePlanRequestDTO request) {
        validateCreatePlan(request);
        
        // Create plan entity from request
        PlanEntity plan = new PlanEntity();
        plan.setName(request.getName());
        plan.setCategory(request.getCategory());
        plan.setDescription(request.getDescription());
        plan.setVisible(request.getVisible());
        plan.setSortOrder(request.getSortOrder());
        plan.setValidFrom(request.getValidFrom());
        plan.setValidUntil(request.getValidUntil());
        plan.setFlatPriceARS(request.getFlatPriceARS());
        plan.setMetadata(request.getMetadata());
        
        PlanEntity savedPlan = planRepository.save(plan);
        
        // Save price tiers if provided
        if (request.getPriceTiers() != null && !request.getPriceTiers().isEmpty()) {
            List<PlanPriceTierEntity> tiers = request.getPriceTiers().stream()
                    .map(tierDTO -> planMapper.toPriceTierEntity(tierDTO, savedPlan))
                    .collect(Collectors.toList());
            priceTierRepository.saveAll(tiers);
            savedPlan.setPriceTiers(tiers);
        }
        
        return planMapper.toDTO(savedPlan);
    }
    
    @Override
    @Transactional
    public PlanDTO updatePlan(Long id, UpdatePlanRequestDTO request) {
        PlanEntity existingPlan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found with id: " + id));
        
        validateUpdatePlan(request);
        
        // Update plan fields only if provided
        if (request.getName() != null) {
            existingPlan.setName(request.getName());
        }
        if (request.getCategory() != null) {
            existingPlan.setCategory(request.getCategory());
        }
        if (request.getDescription() != null) {
            existingPlan.setDescription(request.getDescription());
        }
        if (request.getVisible() != null) {
            existingPlan.setVisible(request.getVisible());
        }
        if (request.getSortOrder() != null) {
            existingPlan.setSortOrder(request.getSortOrder());
        }
        if (request.getValidFrom() != null) {
            existingPlan.setValidFrom(request.getValidFrom());
        }
        if (request.getValidUntil() != null) {
            existingPlan.setValidUntil(request.getValidUntil());
        }
        if (request.getFlatPriceARS() != null) {
            existingPlan.setFlatPriceARS(request.getFlatPriceARS());
        }
        if (request.getMetadata() != null) {
            existingPlan.setMetadata(request.getMetadata());
        }
        
        // Update price tiers if provided
        if (request.getPriceTiers() != null) {
            priceTierRepository.deleteByPlanId(id);
            if (!request.getPriceTiers().isEmpty()) {
                List<PlanPriceTierEntity> newTiers = request.getPriceTiers().stream()
                        .map(tierDTO -> planMapper.toPriceTierEntity(tierDTO, existingPlan))
                        .collect(Collectors.toList());
                priceTierRepository.saveAll(newTiers);
                existingPlan.setPriceTiers(newTiers);
            }
        }
        
        PlanEntity savedPlan = planRepository.save(existingPlan);
        return planMapper.toDTO(savedPlan);
    }
    
    @Override
    @Transactional
    public void deletePlan(Long id) {
        if (!planRepository.existsById(id)) {
            throw new RuntimeException("Plan not found with id: " + id);
        }
        planRepository.deleteById(id);
    }
    
    private void validateCreatePlan(CreatePlanRequestDTO request) {
        // Validate dates
        if (request.getValidFrom() != null && request.getValidUntil() != null) {
            if (!request.getValidUntil().isAfter(request.getValidFrom())) {
                throw new IllegalArgumentException("validUntil must be after validFrom");
            }
        }
        
        // Validate flat price
        if (request.getFlatPriceARS() != null && request.getFlatPriceARS().signum() < 0) {
            throw new IllegalArgumentException("Flat price must be zero or positive");
        }
        
        // Validate price tiers - only for SemiPersonalizado category
        if (request.getPriceTiers() != null && !request.getPriceTiers().isEmpty()) {
            if (!"SemiPersonalizado".equals(request.getCategory())) {
                throw new IllegalArgumentException("Price tiers are only allowed for SemiPersonalizado category");
            }
            
            // Validate tier prices
            for (PlanDTO.PlanPriceTierDTO tier : request.getPriceTiers()) {
                if (tier.getPriceARS() != null && tier.getPriceARS().signum() < 0) {
                    throw new IllegalArgumentException("Price tier amounts must be zero or positive");
                }
            }
        }
    }
    
    private void validateUpdatePlan(UpdatePlanRequestDTO request) {
        // Validate dates
        if (request.getValidFrom() != null && request.getValidUntil() != null) {
            if (!request.getValidUntil().isAfter(request.getValidFrom())) {
                throw new IllegalArgumentException("validUntil must be after validFrom");
            }
        }
        
        // Validate flat price
        if (request.getFlatPriceARS() != null && request.getFlatPriceARS().signum() < 0) {
            throw new IllegalArgumentException("Flat price must be zero or positive");
        }
        
        // Validate price tiers - only for SemiPersonalizado category
        if (request.getPriceTiers() != null && !request.getPriceTiers().isEmpty()) {
            if (request.getCategory() != null && !"SemiPersonalizado".equals(request.getCategory())) {
                throw new IllegalArgumentException("Price tiers are only allowed for SemiPersonalizado category");
            }
            
            // Validate tier prices
            for (PlanDTO.PlanPriceTierDTO tier : request.getPriceTiers()) {
                if (tier.getPriceARS() != null && tier.getPriceARS().signum() < 0) {
                    throw new IllegalArgumentException("Price tier amounts must be zero or positive");
                }
            }
        }
    }
}
