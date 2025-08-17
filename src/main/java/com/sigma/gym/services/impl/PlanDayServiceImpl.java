package com.sigma.gym.services.impl;

import com.sigma.gym.DTOs.PlanDayDTO;
import com.sigma.gym.entity.PlanDayEntity;
import com.sigma.gym.entity.WorkoutPlanEntity;
import com.sigma.gym.repository.PlanDayRepository;
import com.sigma.gym.repository.WorkoutPlanRepository;
import com.sigma.gym.services.PlanDayService;
import com.sigma.gym.utils.ReorderUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanDayServiceImpl implements PlanDayService {

    private final PlanDayRepository planDayRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final ReorderUtils reorderUtils;

    @Override
    public PlanDayEntity createPlanDay(Long planId, PlanDayDTO planDayDTO) {
        WorkoutPlanEntity workoutPlan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Workout plan not found with ID: " + planId));

        // Check if plan can be edited
        if (!workoutPlan.isDraft()) {
            throw new RuntimeException("Cannot modify active or archived workout plans");
        }

        int orderIndex = planDayDTO.getOrderIndex() != null ? 
                planDayDTO.getOrderIndex() : 
                reorderUtils.getNextPlanDayOrderIndex(planId);

        // If inserting at specific position, shift others
        if (planDayDTO.getOrderIndex() != null && 
            planDayRepository.existsByWorkoutPlanIdAndOrderIndex(planId, planDayDTO.getOrderIndex())) {
            orderIndex = reorderUtils.insertPlanDayAt(planId, planDayDTO.getOrderIndex());
        }

        PlanDayEntity planDay = PlanDayEntity.builder()
                .workoutPlan(workoutPlan)
                .title(planDayDTO.getTitle())
                .description(planDayDTO.getDescription())
                .orderIndex(orderIndex)
                .build();

        return planDayRepository.save(planDay);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanDayEntity> getPlanDaysByPlanId(Long planId) {
        return planDayRepository.findByWorkoutPlanIdOrderByOrderIndexAsc(planId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlanDayEntity> getPlanDayById(Long dayId) {
        return planDayRepository.findById(dayId);
    }

    @Override
    public PlanDayEntity updatePlanDay(Long dayId, PlanDayDTO planDayDTO) {
        PlanDayEntity planDay = planDayRepository.findById(dayId)
                .orElseThrow(() -> new RuntimeException("Plan day not found with ID: " + dayId));

        // Check if plan can be edited
        if (!planDay.getWorkoutPlan().isDraft()) {
            throw new RuntimeException("Cannot modify active or archived workout plans");
        }

        // Update fields
        if (planDayDTO.getTitle() != null) {
            planDay.setTitle(planDayDTO.getTitle());
        }
        if (planDayDTO.getDescription() != null) {
            planDay.setDescription(planDayDTO.getDescription());
        }

        return planDayRepository.save(planDay);
    }

    @Override
    public void deletePlanDay(Long dayId) {
        PlanDayEntity planDay = planDayRepository.findById(dayId)
                .orElseThrow(() -> new RuntimeException("Plan day not found with ID: " + dayId));

        // Check if plan can be edited
        if (!planDay.getWorkoutPlan().isDraft()) {
            throw new RuntimeException("Cannot modify active or archived workout plans");
        }

        Long planId = planDay.getWorkoutPlan().getId();
        int removedIndex = planDay.getOrderIndex();

        // Delete the day
        planDayRepository.delete(planDay);

        // Close the gap in order indexes
        reorderUtils.removePlanDayAt(planId, removedIndex);
    }

    @Override
    public void reorderPlanDay(Long planId, int fromIndex, int toIndex) {
        WorkoutPlanEntity workoutPlan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Workout plan not found with ID: " + planId));

        // Check if plan can be edited
        if (!workoutPlan.isDraft()) {
            throw new RuntimeException("Cannot modify active or archived workout plans");
        }

        reorderUtils.reorderPlanDay(planId, fromIndex, toIndex);
    }

    @Override
    public PlanDayEntity insertPlanDayAt(Long planId, int position, PlanDayDTO planDayDTO) {
        WorkoutPlanEntity workoutPlan = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Workout plan not found with ID: " + planId));

        // Check if plan can be edited
        if (!workoutPlan.isDraft()) {
            throw new RuntimeException("Cannot modify active or archived workout plans");
        }

        // Shift existing days and get the order index
        int orderIndex = reorderUtils.insertPlanDayAt(planId, position);

        PlanDayEntity planDay = PlanDayEntity.builder()
                .workoutPlan(workoutPlan)
                .title(planDayDTO.getTitle())
                .description(planDayDTO.getDescription())
                .orderIndex(orderIndex)
                .build();

        return planDayRepository.save(planDay);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNextOrderIndex(Long planId) {
        return reorderUtils.getNextPlanDayOrderIndex(planId);
    }

    @Override
    public void validateAndFixOrder(Long planId) {
        if (!reorderUtils.validatePlanDayOrder(planId)) {
            reorderUtils.fixPlanDayOrder(planId);
        }
    }
}
