package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.WorkOutLogDTO;
import com.sigma.gym.entity.WorkOutLogEntity;
import com.sigma.gym.model.WorkoutLog;

import java.util.List;
import java.util.stream.Collectors;

public class WorkOutLogMapper {

    // Entity → DTO
    public static WorkOutLogDTO toDto(WorkOutLogEntity entity) {
        if (entity == null) return null;

        return WorkOutLogDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .workoutPlanId(entity.getWorkoutPlan() != null ? entity.getWorkoutPlan().getId() : null)
                .workoutPlanName(entity.getWorkoutPlan() != null ? entity.getWorkoutPlan().getName() : null)
                .date(entity.getDate())
                .notes(entity.getNotes())
                .routineLogs(entity.getRoutineLogs() != null
                        ? entity.getRoutineLogs().stream()
                            .map(RoutineLogMapper::toDto)
                            .collect(Collectors.toList())
                        : null)
                .build();
    }

    // DTO → Entity
    public static WorkOutLogEntity toEntity(WorkOutLogDTO dto) {
        if (dto == null) return null;

        return WorkOutLogEntity.builder()
                .id(dto.getId())
                .date(dto.getDate())
                .notes(dto.getNotes())
                // user y workoutPlan se setean en el service
                .build();
    }

    // Entity → Domain
    public static WorkoutLog toDomain(WorkOutLogEntity entity) {
        if (entity == null) return null;

        return WorkoutLog.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .workoutPlanId(entity.getWorkoutPlan() != null ? entity.getWorkoutPlan().getId() : null)
                .date(entity.getDate() != null ? entity.getDate().atStartOfDay() : null)

                .notes(entity.getNotes())
                .routineLogs(entity.getRoutineLogs() != null
                        ? entity.getRoutineLogs().stream()
                            .map(RoutineLogMapper::toDomain)
                            .collect(Collectors.toList())
                        : null)
                .routineExerciseLogs(entity.getRoutineExerciseLogs() != null
                        ? entity.getRoutineExerciseLogs().stream()
                            .map(RoutineExerciseLogMapper::toDomain)
                            .collect(Collectors.toList())
                        : null)
                .build();
    }

    // Domain → Entity
    public static WorkOutLogEntity toEntity(WorkoutLog domain) {
        if (domain == null) return null;

        return WorkOutLogEntity.builder()
                .id(domain.getId())
                .date(domain.getDate() != null ? domain.getDate().toLocalDate() : null)

                .notes(domain.getNotes())
                // user y workoutPlan se setean en el service
                .build();
    }

    // Domain → DTO
    public static WorkOutLogDTO toDto(WorkoutLog domain) {
        if (domain == null) return null;

        return WorkOutLogDTO.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .workoutPlanId(domain.getWorkoutPlanId())
                .date(domain.getDate() != null ? domain.getDate().toLocalDate() : null)

                .notes(domain.getNotes())
                .routineLogs(domain.getRoutineLogs() != null
                        ? domain.getRoutineLogs().stream()
                            .map(RoutineLogMapper::toDto)
                            .collect(Collectors.toList())
                        : null)
                .build();
    }

    // Listas
    public static List<WorkOutLogDTO> toDtoList(List<WorkOutLogEntity> entities) {
        return entities == null ? List.of()
                : entities.stream().map(WorkOutLogMapper::toDto).collect(Collectors.toList());
    }

    public static List<WorkoutLog> toDomainList(List<WorkOutLogEntity> entities) {
        return entities == null ? List.of()
                : entities.stream().map(WorkOutLogMapper::toDomain).collect(Collectors.toList());
    }
}
