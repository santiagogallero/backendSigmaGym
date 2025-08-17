package com.sigma.gym.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for workout plan statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanStatsDTO {
    
    private Long planId;
    private String planName;
    private String planSlug;
    
    // Day statistics
    private Integer totalDays;
    private Integer activeDays; // Days with exercises
    
    // Exercise statistics
    private Integer totalExercises;
    private Integer warmupExercises;
    private Integer mainExercises;
    
    // Day breakdown
    private Integer minExercisesPerDay;
    private Integer maxExercisesPerDay;
    private Double avgExercisesPerDay;
    
    // Member statistics
    private Integer totalMembers;
    private Boolean hasMembers;
    
    // Plan metrics
    private Integer estimatedDurationDays; // Based on start/end dates
    private Boolean isComplete; // Has days and exercises
    private Boolean isValid; // Passes validation checks
}
