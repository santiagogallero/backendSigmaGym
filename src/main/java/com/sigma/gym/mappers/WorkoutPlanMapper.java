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
                    ? UserMapper.toDomain(entity.getTrainer()) // Changed from toDto to toDomain
                    : null)
            .routines(entity.getRoutines() != null
                    ? entity.getRoutines().stream()
                        .map(RoutineMapper::toDomain)
                        .collect(Collectors.toList())
                    : null)
            .members(entity.getMembers() != null
                    ? entity.getMembers().stream()
                        .map(UserMapper::toDomain) // Changed from toDto to toDomain
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
                    ? UserMapper.toEntity(model.getTrainer()) // This should work with User domain model
                    : null)
            .routines(model.getRoutines() != null
                    ? model.getRoutines().stream()
                        .map(RoutineMapper::toEntity) // Need RoutineMapper.toEntity(Routine)
                        .collect(Collectors.toList())
                    : null)
            .members(model.getMembers() != null
                    ? model.getMembers().stream()
                        .map(UserMapper::toEntity) // This should work with User domain model
                        .collect(Collectors.toList())
                    : null)
            .build();
    }

    // Add this missing method
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
    
    // Add this if it's missing too
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
                    ? UserMapper.toEntity(dto.getTrainer()) // Need UserMapper.toEntity(UserDTO)
                    : null)
            .routines(dto.getRoutines() != null
                    ? dto.getRoutines().stream()
                        .map(RoutineMapper::toEntity) // Need RoutineMapper.toEntity(RoutineDTO)
                        .collect(Collectors.toList())
                    : null)
            .members(dto.getMembers() != null
                    ? dto.getMembers().stream()
                        .map(UserMapper::toEntity) // Need UserMapper.toEntity(UserDTO)
                        .collect(Collectors.toList())
                    : null)
            .build(); // Remove the duplicate .build() line below
}
     // Domain → DTO conversion
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
                        ? UserMapper.toDto(domain.getTrainer()) // Convert User to UserDTO
                        : null)
                .routines(domain.getRoutines() != null
                        ? domain.getRoutines().stream()
                            .map(RoutineMapper::toDto) // Convert Routine to RoutineDTO
                            .collect(Collectors.toList())
                        : null)
                .members(domain.getMembers() != null
                        ? domain.getMembers().stream()
                            .map(UserMapper::toDto) // Convert User to UserDTO
                            .collect(Collectors.toList())
                        : null)
                .build();
    }
    
    // DTO → Domain conversion
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
                        ? UserMapper.toDomain(dto.getTrainer()) // Convert UserDTO to User
                        : null)
                .routines(dto.getRoutines() != null
                        ? dto.getRoutines().stream()
                            .map(RoutineMapper::toDomain) // Convert RoutineDTO to Routine
                            .collect(Collectors.toList())
                        : null)
                .members(dto.getMembers() != null
                        ? dto.getMembers().stream()
                            .map(UserMapper::toDomain) // Convert UserDTO to User
                            .collect(Collectors.toList())
                        : null)
                .build();
    }
}
