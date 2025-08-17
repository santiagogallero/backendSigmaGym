package com.sigma.gym.utils;

import com.sigma.gym.repository.PlanDayRepository;
import com.sigma.gym.repository.PlanExerciseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Utility class for handling drag & drop reordering operations
 * Provides methods to reorder plan days and exercises while maintaining order integrity
 */
@Component
public class ReorderUtils {

    @Autowired
    private PlanDayRepository planDayRepository;

    @Autowired
    private PlanExerciseRepository planExerciseRepository;

    /**
     * Reorder a plan day from one position to another
     * @param planId The workout plan ID
     * @param fromIndex Current position (0-based)
     * @param toIndex Target position (0-based)
     */
    @Transactional
    public void reorderPlanDay(Long planId, int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            return; // No change needed
        }

        if (fromIndex < toIndex) {
            // Moving down: shift intermediate items up
            planDayRepository.decrementOrderIndexesFrom(planId, fromIndex + 1);
            // Update the moved item to its new position
            var dayToMove = planDayRepository.findByWorkoutPlanIdAndOrderIndex(planId, fromIndex);
            if (dayToMove.isPresent()) {
                dayToMove.get().setOrderIndex(toIndex);
                planDayRepository.save(dayToMove.get());
            }
        } else {
            // Moving up: shift intermediate items down
            planDayRepository.incrementOrderIndexesFrom(planId, toIndex);
            // Update the moved item to its new position
            var dayToMove = planDayRepository.findByWorkoutPlanIdAndOrderIndex(planId, fromIndex + 1); // +1 because items shifted
            if (dayToMove.isPresent()) {
                dayToMove.get().setOrderIndex(toIndex);
                planDayRepository.save(dayToMove.get());
            }
        }
    }

    /**
     * Reorder a plan exercise from one position to another within a day
     * @param dayId The plan day ID
     * @param fromIndex Current position (0-based)
     * @param toIndex Target position (0-based)
     */
    @Transactional
    public void reorderPlanExercise(Long dayId, int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            return; // No change needed
        }

        if (fromIndex < toIndex) {
            // Moving down: shift intermediate items up
            planExerciseRepository.decrementOrderIndexesFrom(dayId, fromIndex + 1);
            // Update the moved item to its new position
            var exerciseToMove = planExerciseRepository.findByPlanDay_IdAndOrderIndex(dayId, fromIndex);
            if (exerciseToMove.isPresent()) {
                exerciseToMove.get().setOrderIndex(toIndex);
                planExerciseRepository.save(exerciseToMove.get());
            }
        } else {
            // Moving up: shift intermediate items down
            planExerciseRepository.incrementOrderIndexesFrom(dayId, toIndex);
            // Update the moved item to its new position
            var exerciseToMove = planExerciseRepository.findByPlanDay_IdAndOrderIndex(dayId, fromIndex + 1); // +1 because items shifted
            if (exerciseToMove.isPresent()) {
                exerciseToMove.get().setOrderIndex(toIndex);
                planExerciseRepository.save(exerciseToMove.get());
            }
        }
    }

    /**
     * Insert a new plan day at a specific position, shifting subsequent days down
     * @param planId The workout plan ID
     * @param insertIndex The position to insert at (0-based)
     * @return The order index to use for the new day
     */
    @Transactional
    public int insertPlanDayAt(Long planId, int insertIndex) {
        // Shift all days at or after the insert position down by 1
        planDayRepository.incrementOrderIndexesFrom(planId, insertIndex);
        return insertIndex;
    }

    /**
     * Insert a new plan exercise at a specific position, shifting subsequent exercises down
     * @param dayId The plan day ID
     * @param insertIndex The position to insert at (0-based)
     * @return The order index to use for the new exercise
     */
    @Transactional
    public int insertPlanExerciseAt(Long dayId, int insertIndex) {
        // Shift all exercises at or after the insert position down by 1
        planExerciseRepository.incrementOrderIndexesFrom(dayId, insertIndex);
        return insertIndex;
    }

    /**
     * Remove a plan day and close the gap by shifting subsequent days up
     * @param planId The workout plan ID
     * @param removedIndex The position of the removed day (0-based)
     */
    @Transactional
    public void removePlanDayAt(Long planId, int removedIndex) {
        // Shift all days after the removed position up by 1
        planDayRepository.decrementOrderIndexesFrom(planId, removedIndex + 1);
    }

    /**
     * Remove a plan exercise and close the gap by shifting subsequent exercises up
     * @param dayId The plan day ID
     * @param removedIndex The position of the removed exercise (0-based)
     */
    @Transactional
    public void removePlanExerciseAt(Long dayId, int removedIndex) {
        // Shift all exercises after the removed position up by 1
        planExerciseRepository.decrementOrderIndexesFrom(dayId, removedIndex + 1);
    }

    /**
     * Get the next available order index for a new plan day
     * @param planId The workout plan ID
     * @return The next order index to use
     */
    public int getNextPlanDayOrderIndex(Long planId) {
        Integer maxIndex = planDayRepository.findMaxOrderIndexByWorkoutPlanId(planId);
        return (maxIndex == null ? 0 : maxIndex + 1);
    }

    /**
     * Get the next available order index for a new plan exercise
     * @param dayId The plan day ID
     * @return The next order index to use
     */
    public int getNextPlanExerciseOrderIndex(Long dayId) {
        Integer maxIndex = planExerciseRepository.findMaxOrderIndexByDayId(dayId);
        return (maxIndex == null ? 0 : maxIndex + 1);
    }

    /**
     * Validate that order indexes are continuous and start from 0
     * @param planId The workout plan ID
     * @return true if order is valid, false otherwise
     */
    public boolean validatePlanDayOrder(Long planId) {
        var days = planDayRepository.findByWorkoutPlanIdOrderByOrderIndexAsc(planId);
        for (int i = 0; i < days.size(); i++) {
            if (days.get(i).getOrderIndex() != i) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validate that order indexes are continuous and start from 0
     * @param dayId The plan day ID
     * @return true if order is valid, false otherwise
     */
    public boolean validatePlanExerciseOrder(Long dayId) {
        var exercises = planExerciseRepository.findByPlanDay_IdOrderByOrderIndexAsc(dayId);
        for (int i = 0; i < exercises.size(); i++) {
            if (exercises.get(i).getOrderIndex() != i) {
                return false;
            }
        }
        return true;
    }

    /**
     * Fix order indexes for plan days to ensure they are continuous starting from 0
     * @param planId The workout plan ID
     */
    @Transactional
    public void fixPlanDayOrder(Long planId) {
        var days = planDayRepository.findByWorkoutPlanIdOrderByOrderIndexAsc(planId);
        for (int i = 0; i < days.size(); i++) {
            if (days.get(i).getOrderIndex() != i) {
                days.get(i).setOrderIndex(i);
                planDayRepository.save(days.get(i));
            }
        }
    }

    /**
     * Fix order indexes for plan exercises to ensure they are continuous starting from 0
     * @param dayId The plan day ID
     */
    @Transactional
    public void fixPlanExerciseOrder(Long dayId) {
        var exercises = planExerciseRepository.findByPlanDay_IdOrderByOrderIndexAsc(dayId);
        for (int i = 0; i < exercises.size(); i++) {
            if (exercises.get(i).getOrderIndex() != i) {
                exercises.get(i).setOrderIndex(i);
                planExerciseRepository.save(exercises.get(i));
            }
        }
    }
}
