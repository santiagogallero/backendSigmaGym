package com.sigma.gym.services;

import com.sigma.gym.DTOs.PlanExerciseDTO;
import com.sigma.gym.entity.PlanExerciseEntity;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing plan exercises
 */
public interface PlanExerciseService {
    
    /**
     * Create a new plan exercise
     */
    PlanExerciseEntity createPlanExercise(Long dayId, PlanExerciseDTO planExerciseDTO);
    
    /**
     * Get all exercises for a plan day
     */
    List<PlanExerciseEntity> getPlanExercisesByDayId(Long dayId);
    
    /**
     * Get exercises separated by warmup/main
     */
    List<PlanExerciseEntity> getWarmupExercises(Long dayId);
    List<PlanExerciseEntity> getMainExercises(Long dayId);
    
    /**
     * Get a specific plan exercise by ID
     */
    Optional<PlanExerciseEntity> getPlanExerciseById(Long exerciseId);
    
    /**
     * Update a plan exercise
     */
    PlanExerciseEntity updatePlanExercise(Long exerciseId, PlanExerciseDTO planExerciseDTO);
    
    /**
     * Delete a plan exercise
     */
    void deletePlanExercise(Long exerciseId);
    
    /**
     * Reorder a plan exercise from one position to another
     */
    void reorderPlanExercise(Long dayId, int fromIndex, int toIndex);
    
    /**
     * Insert a new plan exercise at a specific position
     */
    PlanExerciseEntity insertPlanExerciseAt(Long dayId, int position, PlanExerciseDTO planExerciseDTO);
    
    /**
     * Get the next available order index for a day
     */
    int getNextOrderIndex(Long dayId);
    
    /**
     * Validate and fix order indexes for a day
     */
    void validateAndFixOrder(Long dayId);
    
    /**
     * Search exercises by name within a day
     */
    List<PlanExerciseEntity> searchExercisesByName(Long dayId, String name);
}
