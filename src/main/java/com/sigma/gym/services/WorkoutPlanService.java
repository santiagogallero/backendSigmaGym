package com.sigma.gym.services;

import com.sigma.gym.DTOs.WorkoutPlanDTO;
import com.sigma.gym.DTOs.WorkoutPlanStatsDTO;
import com.sigma.gym.entity.WorkoutPlanEntity;
import com.sigma.gym.model.WorkoutPlan;
import com.sigma.gym.model.WorkoutPlanStatus;
import java.util.List;
import java.util.Optional;

/**
 * Enhanced service interface for managing workout plans with slug support
 */
public interface WorkoutPlanService {
    
    // Legacy methods for backward compatibility
    WorkoutPlan createWorkoutPlan(WorkoutPlan workoutPlan);
    WorkoutPlan getWorkoutPlanById(Long id);
    List<WorkoutPlan> getAllWorkoutPlans();
    WorkoutPlan updateWorkoutPlan(Long id, WorkoutPlan workoutPlan);
    void deleteWorkoutPlan(Long id);
    
    // New enhanced methods with DTO support
    /**
     * Create a new workout plan with automatic slug generation
     */
    WorkoutPlanEntity createWorkoutPlanEntity(Long ownerId, WorkoutPlanDTO workoutPlanDTO);
    
    /**
     * Get workout plan by owner ID and slug
     */
    Optional<WorkoutPlanEntity> getWorkoutPlanBySlug(Long ownerId, String slug);
    
    /**
     * Get workout plan entity by ID
     */
    Optional<WorkoutPlanEntity> getWorkoutPlanEntityById(Long id);
    
    /**
     * Get all workout plans for an owner
     */
    List<WorkoutPlanEntity> getWorkoutPlansByOwnerId(Long ownerId);
    
    /**
     * Get workout plans by owner and status
     */
    List<WorkoutPlanEntity> getWorkoutPlansByOwnerAndStatus(Long ownerId, WorkoutPlanStatus status);
    
    /**
     * Update workout plan with slug regeneration if name changed
     */
    WorkoutPlanEntity updateWorkoutPlanEntity(Long id, WorkoutPlanDTO workoutPlanDTO);
    
    /**
     * Partial update for specific fields
     */
    WorkoutPlanEntity partialUpdateWorkoutPlan(Long id, WorkoutPlanDTO workoutPlanDTO);
    
    /**
     * Delete workout plan by ID
     */
    void deleteWorkoutPlanEntity(Long id);
    
    /**
     * Change workout plan status
     */
    WorkoutPlanEntity changeStatus(Long id, WorkoutPlanStatus newStatus);
    
    /**
     * Duplicate a workout plan with new name/slug
     */
    WorkoutPlanEntity duplicateWorkoutPlan(Long originalId, String newName);
    
    /**
     * Search workout plans by name
     */
    List<WorkoutPlanEntity> searchWorkoutPlansByName(Long ownerId, String name);
    
    /**
     * Check if user can edit workout plan (based on status and ownership)
     */
    boolean canEditWorkoutPlan(Long planId, Long userId);
    
    /**
     * Validate workout plan structure and fix any issues
     */
    void validateAndFixWorkoutPlan(Long planId);
    
    /**
     * Get workout plan statistics
     */
    WorkoutPlanStatsDTO getWorkoutPlanStats(Long planId);
}
