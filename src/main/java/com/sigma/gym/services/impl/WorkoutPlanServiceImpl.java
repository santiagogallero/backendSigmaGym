package com.sigma.gym.services.impl;

import com.sigma.gym.entity.WorkoutPlanEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.mappers.WorkoutPlanMapper;
import com.sigma.gym.model.WorkoutPlan;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.repository.WorkoutPlanRepository;
import com.sigma.gym.services.WorkoutPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkoutPlanServiceImpl implements WorkoutPlanService {

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public WorkoutPlan createWorkoutPlan(WorkoutPlan workoutPlan) {
        WorkoutPlanEntity entity = WorkoutPlanMapper.toEntity(workoutPlan);

        // Mapear trainer (User)
        if (workoutPlan.getTrainer() != null && workoutPlan.getTrainer().getId() != null) {
            UserEntity trainer = userRepository.findById(workoutPlan.getTrainer().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + workoutPlan.getTrainer().getId()));
            entity.setTrainer(trainer);
        }

        return WorkoutPlanMapper.toDomain(workoutPlanRepository.save(entity));
    }

    @Override
    public WorkoutPlan getWorkoutPlanById(Long id) {
        WorkoutPlanEntity entity = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan not found with id: " + id));
        return WorkoutPlanMapper.toDomain(entity);
    }

    @Override
    public List<WorkoutPlan> getAllWorkoutPlans() {
        return workoutPlanRepository.findAll()
                .stream()
                .map(WorkoutPlanMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public WorkoutPlan updateWorkoutPlan(Long id, WorkoutPlan workoutPlan) {
        WorkoutPlanEntity entity = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan not found with id: " + id));

        entity.setName(workoutPlan.getName());
        entity.setGoal(workoutPlan.getGoal());
        entity.setDescription(workoutPlan.getDescription());
        entity.setStartDate(workoutPlan.getStartDate());
        entity.setEndDate(workoutPlan.getEndDate());
        entity.setDifficulty(workoutPlan.getDifficulty());
        entity.setNotes(workoutPlan.getNotes());
        entity.setCreatedAt(workoutPlan.getCreatedAt());

        // Actualizar trainer si cambia
        if (workoutPlan.getTrainer() != null && workoutPlan.getTrainer().getId() != null) {
            UserEntity trainer = userRepository.findById(workoutPlan.getTrainer().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + workoutPlan.getTrainer().getId()));
            entity.setTrainer(trainer);
        }

        return WorkoutPlanMapper.toDomain(workoutPlanRepository.save(entity));
    }

    @Override
    public void deleteWorkoutPlan(Long id) {
        WorkoutPlanEntity entity = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan not found with id: " + id));
        workoutPlanRepository.delete(entity);
    }
}
