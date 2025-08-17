package com.sigma.gym.services;

import com.sigma.gym.DTOs.plans.CreatePlanRequestDTO;
import com.sigma.gym.DTOs.plans.PlanDTO;
import com.sigma.gym.DTOs.plans.UpdatePlanRequestDTO;

import java.util.List;

public interface PlanService {
    
    List<PlanDTO> getVisiblePlans();
    
    List<PlanDTO> getAllPlans();
    
    PlanDTO getPlanById(Long id);
    
    PlanDTO createPlan(CreatePlanRequestDTO createRequest);
    
    PlanDTO updatePlan(Long id, UpdatePlanRequestDTO updateRequest);
    
    void deletePlan(Long id);
}
