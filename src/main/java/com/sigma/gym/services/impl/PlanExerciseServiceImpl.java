package com.sigma.gym.services.impl;

import com.sigma.gym.DTOs.PlanExerciseDTO;
import com.sigma.gym.entity.PlanDayEntity;
import com.sigma.gym.entity.PlanExerciseEntity;
import com.sigma.gym.repository.PlanDayRepository;
import com.sigma.gym.repository.PlanExerciseRepository;
import com.sigma.gym.services.PlanExerciseService;
import com.sigma.gym.utils.ReorderUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanExerciseServiceImpl implements PlanExerciseService {

    private final PlanExerciseRepository planExerciseRepository;
    private final PlanDayRepository planDayRepository;
    private final ReorderUtils reorderUtils;

    @Override
    public PlanExerciseEntity createPlanExercise(Long dayId, PlanExerciseDTO planExerciseDTO) {
        PlanDayEntity planDay = planDayRepository.findById(dayId)
                .orElseThrow(() -> new RuntimeException("Plan day not found with ID: " + dayId));

        // Check if plan can be edited
        if (!planDay.getWorkoutPlan().isDraft()) {
            throw new RuntimeException("Cannot modify active or archived workout plans");
        }

        int orderIndex = planExerciseDTO.getOrderIndex() != null ? 
                planExerciseDTO.getOrderIndex() : 
                reorderUtils.getNextPlanExerciseOrderIndex(dayId);

        // If inserting at specific position, shift others
        if (planExerciseDTO.getOrderIndex() != null && 
            planExerciseRepository.existsByPlanDay_IdAndOrderIndex(dayId, planExerciseDTO.getOrderIndex())) {
            orderIndex = reorderUtils.insertPlanExerciseAt(dayId, planExerciseDTO.getOrderIndex());
        }

        PlanExerciseEntity planExercise = PlanExerciseEntity.builder()
                .planDay(planDay)
                .name(planExerciseDTO.getName())
                .description(planExerciseDTO.getDescription())
                .reps(planExerciseDTO.getReps())
                .sets(planExerciseDTO.getSets())
                .weight(planExerciseDTO.getWeight())
                .weightUnit(planExerciseDTO.getWeightUnit())
                .restTimeSeconds(planExerciseDTO.getRestTimeSeconds())
                .notes(planExerciseDTO.getNotes())
                .isWarmup(planExerciseDTO.getIsWarmup() != null ? planExerciseDTO.getIsWarmup() : false)
                .orderIndex(orderIndex)
                .build();

        return planExerciseRepository.save(planExercise);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanExerciseEntity> getPlanExercisesByDayId(Long dayId) {
        return planExerciseRepository.findByPlanDay_IdOrderByOrderIndexAsc(dayId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanExerciseEntity> getWarmupExercises(Long dayId) {
        return planExerciseRepository.findByPlanDay_IdAndIsWarmupTrueOrderByOrderIndexAsc(dayId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanExerciseEntity> getMainExercises(Long dayId) {
        return planExerciseRepository.findByPlanDay_IdAndIsWarmupFalseOrderByOrderIndexAsc(dayId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlanExerciseEntity> getPlanExerciseById(Long exerciseId) {
        return planExerciseRepository.findById(exerciseId);
    }

    @Override
    public PlanExerciseEntity updatePlanExercise(Long exerciseId, PlanExerciseDTO planExerciseDTO) {
        PlanExerciseEntity planExercise = planExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Plan exercise not found with ID: " + exerciseId));

        // Check if plan can be edited
        if (!planExercise.getPlanDay().getWorkoutPlan().isDraft()) {
            throw new RuntimeException("Cannot modify active or archived workout plans");
        }

        // Update fields
        if (planExerciseDTO.getName() != null) {
            planExercise.setName(planExerciseDTO.getName());
        }
        if (planExerciseDTO.getDescription() != null) {
            planExercise.setDescription(planExerciseDTO.getDescription());
        }
        if (planExerciseDTO.getReps() != null) {
            planExercise.setReps(planExerciseDTO.getReps());
        }
        if (planExerciseDTO.getSets() != null) {
            planExercise.setSets(planExerciseDTO.getSets());
        }
        if (planExerciseDTO.getWeight() != null) {
            planExercise.setWeight(planExerciseDTO.getWeight());
        }
        if (planExerciseDTO.getWeightUnit() != null) {
            planExercise.setWeightUnit(planExerciseDTO.getWeightUnit());
        }
        if (planExerciseDTO.getRestTimeSeconds() != null) {
            planExercise.setRestTimeSeconds(planExerciseDTO.getRestTimeSeconds());
        }
        if (planExerciseDTO.getNotes() != null) {
            planExercise.setNotes(planExerciseDTO.getNotes());
        }
        if (planExerciseDTO.getIsWarmup() != null) {
            planExercise.setIsWarmup(planExerciseDTO.getIsWarmup());
        }

        return planExerciseRepository.save(planExercise);
    }

    @Override
    public void deletePlanExercise(Long exerciseId) {
        PlanExerciseEntity planExercise = planExerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new RuntimeException("Plan exercise not found with ID: " + exerciseId));

        // Check if plan can be edited
        if (!planExercise.getPlanDay().getWorkoutPlan().isDraft()) {
            throw new RuntimeException("Cannot modify active or archived workout plans");
        }

        Long dayId = planExercise.getPlanDay().getId();
        int removedIndex = planExercise.getOrderIndex();

        // Delete the exercise
        planExerciseRepository.delete(planExercise);

        // Close the gap in order indexes
        reorderUtils.removePlanExerciseAt(dayId, removedIndex);
    }

    @Override
    public void reorderPlanExercise(Long dayId, int fromIndex, int toIndex) {
        PlanDayEntity planDay = planDayRepository.findById(dayId)
                .orElseThrow(() -> new RuntimeException("Plan day not found with ID: " + dayId));

        // Check if plan can be edited
        if (!planDay.getWorkoutPlan().isDraft()) {
            throw new RuntimeException("Cannot modify active or archived workout plans");
        }

        reorderUtils.reorderPlanExercise(dayId, fromIndex, toIndex);
    }

    @Override
    public PlanExerciseEntity insertPlanExerciseAt(Long dayId, int position, PlanExerciseDTO planExerciseDTO) {
        PlanDayEntity planDay = planDayRepository.findById(dayId)
                .orElseThrow(() -> new RuntimeException("Plan day not found with ID: " + dayId));

        // Check if plan can be edited
        if (!planDay.getWorkoutPlan().isDraft()) {
            throw new RuntimeException("Cannot modify active or archived workout plans");
        }

        // Shift existing exercises and get the order index
        int orderIndex = reorderUtils.insertPlanExerciseAt(dayId, position);

        PlanExerciseEntity planExercise = PlanExerciseEntity.builder()
                .planDay(planDay)
                .name(planExerciseDTO.getName())
                .description(planExerciseDTO.getDescription())
                .reps(planExerciseDTO.getReps())
                .sets(planExerciseDTO.getSets())
                .weight(planExerciseDTO.getWeight())
                .weightUnit(planExerciseDTO.getWeightUnit())
                .restTimeSeconds(planExerciseDTO.getRestTimeSeconds())
                .notes(planExerciseDTO.getNotes())
                .isWarmup(planExerciseDTO.getIsWarmup() != null ? planExerciseDTO.getIsWarmup() : false)
                .orderIndex(orderIndex)
                .build();

        return planExerciseRepository.save(planExercise);
    }

    @Override
    @Transactional(readOnly = true)
    public int getNextOrderIndex(Long dayId) {
        return reorderUtils.getNextPlanExerciseOrderIndex(dayId);
    }

    @Override
    public void validateAndFixOrder(Long dayId) {
        if (!reorderUtils.validatePlanExerciseOrder(dayId)) {
            reorderUtils.fixPlanExerciseOrder(dayId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlanExerciseEntity> searchExercisesByName(Long dayId, String name) {
        return planExerciseRepository.findByPlanDay_IdAndNameContainingIgnoreCase(dayId, name);
    }
}
