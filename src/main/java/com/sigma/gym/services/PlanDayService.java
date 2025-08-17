package com.sigma.gym.services;

import com.sigma.gym.DTOs.PlanDayDTO;
import com.sigma.gym.entity.PlanDayEntity;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing workout plan days
 */
public interface PlanDayService {
    
    /**
     * Create a new plan day
     */
    PlanDayEntity createPlanDay(Long planId, PlanDayDTO planDayDTO);
    
    /**
     * Get all days for a workout plan
     */
    List<PlanDayEntity> getPlanDaysByPlanId(Long planId);
    
    /**
     * Get a specific plan day by ID
     */
    Optional<PlanDayEntity> getPlanDayById(Long dayId);
    
    /**
     * Update a plan day
     */
    PlanDayEntity updatePlanDay(Long dayId, PlanDayDTO planDayDTO);
    
    /**
     * Delete a plan day
     */
    void deletePlanDay(Long dayId);
    
    /**
     * Reorder a plan day from one position to another
     */
    void reorderPlanDay(Long planId, int fromIndex, int toIndex);
    
    /**
     * Insert a new plan day at a specific position
     */
    PlanDayEntity insertPlanDayAt(Long planId, int position, PlanDayDTO planDayDTO);
    
    /**
     * Get the next available order index for a plan
     */
    int getNextOrderIndex(Long planId);
    
    /**
     * Validate and fix order indexes for a plan
     */
    void validateAndFixOrder(Long planId);
}
