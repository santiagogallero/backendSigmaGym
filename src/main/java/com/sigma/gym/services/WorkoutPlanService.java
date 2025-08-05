package com.sigma.gym.services;

import com.sigma.gym.model.WorkoutPlan;
import java.util.List;

public interface WorkoutPlanService {
    WorkoutPlan createWorkoutPlan(WorkoutPlan workoutPlan);
    WorkoutPlan getWorkoutPlanById(Long id);
    List<WorkoutPlan> getAllWorkoutPlans();
    WorkoutPlan updateWorkoutPlan(Long id, WorkoutPlan workoutPlan);
    void deleteWorkoutPlan(Long id);
}
