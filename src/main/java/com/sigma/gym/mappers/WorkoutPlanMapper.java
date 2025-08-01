package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.WorkoutPlanDTO;
import com.sigma.gym.entity.WorkoutPlanEntity;
import com.sigma.gym.model.WorkoutPlan;
import java.util.stream.Collectors;

public class WorkoutPlanMapper {

   public static WorkoutPlan toDomain(WorkoutPlanEntity entity) {
    if (entity == null) return null;

    return WorkoutPlan.builder()
            .id(entity.getId())
            .name(entity.getName())
            .goal(entity.getGoal())
            .difficulty(entity.getDifficulty())
            .notes(entity.getNotes())
            .createdAt(entity.getCreatedAt())
            .trainer(entity.getTrainer() != null
                    ? UserMapper.toDomain(entity.getTrainer())
                    : null)
            .routines(entity.getRoutines() != null
                    ? entity.getRoutines().stream()
                        .map(RoutineMapper::toDomain)
                        .collect(Collectors.toList())
                    : null)
            .members(entity.getMembers() != null
                    ? entity.getMembers().stream()
                        .map(UserMapper::toDomain)
                        .collect(Collectors.toList())
                    : null)
            .build();
    }

    public static WorkoutPlanEntity toEntity(WorkoutPlan model) {
    if (model == null) return null;

    return WorkoutPlanEntity.builder()
            .id(model.getId())
            .name(model.getName())
            .goal(model.getGoal())
            .difficulty(model.getDifficulty())
            .notes(model.getNotes())
            .createdAt(model.getCreatedAt())
            .trainer(model.getTrainer() != null
                    ? UserMapper.toEntity(model.getTrainer())
                    : null)
            .routines(model.getRoutines() != null
                    ? model.getRoutines().stream()
                        .map(RoutineMapper::toEntity)
                        .collect(Collectors.toList())
                    : null)
            .members(model.getMembers() != null
                    ? model.getMembers().stream()
                        .map(UserMapper::toEntity)
                        .collect(Collectors.toList())
                    : null)
            .build();
    }

    public static WorkoutPlanDTO toDto(WorkoutPlanEntity entity) {
        if (entity == null) return null;
        return WorkoutPlanDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .goal(entity.getGoal())
                .difficulty(entity.getDifficulty())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .trainer(entity.getTrainer() != null
                        ? UserMapper.toDto(entity.getTrainer())
                        : null)
                .routines(entity.getRoutines() != null          
                        ? entity.getRoutines().stream()
                            .map(RoutineMapper::toDto)
                            .collect(Collectors.toList())
                        : null)
                .members(entity.getMembers() != null
                        ? entity.getMembers().stream()
                            .map(UserMapper::toDto)
                            .collect(Collectors.toList())
                        : null)
                .build();
    }
    
    public static WorkoutPlanEntity toEntity(WorkoutPlanDTO dto) {
        if (dto == null) return null;
        return WorkoutPlanEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .goal(dto.getGoal())
                .difficulty(dto.getDifficulty())
                .notes(dto.getNotes())
                .createdAt(dto.getCreatedAt())
                .trainer(dto.getTrainer() != null
                        ? UserMapper.toEntity(dto.getTrainer())
                        : null)
                .routines(dto.getRoutines() != null
                        ? dto.getRoutines().stream()
                            .map(RoutineMapper::toEntity)
                            .collect(Collectors.toList())
                        : null)
                .members(dto.getMembers() != null
                        ? dto.getMembers().stream()
                            .map(UserMapper::toEntity)
                            .collect(Collectors.toList())
                        : null)
                .build();
    }

    public static WorkoutPlanDTO toDto(WorkoutPlan domain) {
        if (domain == null) return null;
        return WorkoutPlanDTO.builder()
                .id(domain.getId())
                .name(domain.getName())
                .goal(domain.getGoal())
                .difficulty(domain.getDifficulty())
                .notes(domain.getNotes())
                .createdAt(domain.getCreatedAt())
                .trainer(domain.getTrainer() != null
                        ? UserMapper.toDto(domain.getTrainer())
                        : null)
                .routines(domain.getRoutines() != null
                        ? domain.getRoutines().stream()
                            .map(RoutineMapper::toDto)
                            .collect(Collectors.toList())
                        : null)
                .members(domain.getMembers() != null
                        ? domain.getMembers().stream()
                            .map(UserMapper::toDto)
                            .collect(Collectors.toList())
                        : null)
                .build();
    }
    
    public static WorkoutPlan toDomain(WorkoutPlanDTO dto) {
        if (dto == null) return null;
        return WorkoutPlan.builder()
                .id(dto.getId())
                .name(dto.getName())
                .goal(dto.getGoal())
                .difficulty(dto.getDifficulty())
                .notes(dto.getNotes())
                .createdAt(dto.getCreatedAt())
                .trainer(dto.getTrainer() != null
                        ? UserMapper.toDomain(dto.getTrainer())
                        : null)
                .routines(dto.getRoutines() != null
                        ? dto.getRoutines().stream()
                            .map(RoutineMapper::toDomain)
                            .collect(Collectors.toList())
                        : null)
                .members(dto.getMembers() != null
                        ? dto.getMembers().stream()
                            .map(UserMapper::toDomain)
                            .collect(Collectors.toList())
                        : null)
                .build();
    }
}
