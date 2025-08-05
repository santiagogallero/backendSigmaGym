package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.RoutineLogDTO;
import com.sigma.gym.entity.RoutineLogEntity;
import com.sigma.gym.model.RoutineLog;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RoutineLogMapper {

    // Entity → Domain
    public static RoutineLog toDomain(RoutineLogEntity entity) {
        if (entity == null) return null;

        return RoutineLog.builder()
                .id(entity.getId())
                .workoutLogId(entity.getWorkoutLog() != null ? entity.getWorkoutLog().getId() : null)
                .routineId(entity.getRoutine() != null ? entity.getRoutine().getId() : null)
                .routineName(entity.getRoutine() != null ? entity.getRoutine().getName() : null) // ✅ ahora compila
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .notes(entity.getNotes())
                .completed(entity.getCompleted())
                .exerciseLogs(entity.getExerciseLogs() != null
                        ? entity.getExerciseLogs().stream()
                            .map(RoutineExerciseLogMapper::toDomain)
                            .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }

    // Domain → Entity
    public static RoutineLogEntity toEntity(RoutineLog domain) {
        if (domain == null) return null;

        return RoutineLogEntity.builder()
                .id(domain.getId())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .notes(domain.getNotes())
                .completed(domain.getCompleted())
                .build();
    }

    // Entity → DTO
    public static RoutineLogDTO toDto(RoutineLogEntity entity) {
        if (entity == null) return null;

        return RoutineLogDTO.builder()
                .id(entity.getId())
                .workoutLogId(entity.getWorkoutLog() != null ? entity.getWorkoutLog().getId() : null)
                .routineId(entity.getRoutine() != null ? entity.getRoutine().getId() : null)
                .routineName(entity.getRoutine() != null ? entity.getRoutine().getName() : null)
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .notes(entity.getNotes())
                .completed(entity.getCompleted())
                .exerciseLogs(entity.getExerciseLogs() != null
                        ? entity.getExerciseLogs().stream()
                            .map(RoutineExerciseLogMapper::toDto)
                            .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }

    // Domain → DTO
    public static RoutineLogDTO toDto(RoutineLog domain) {
        if (domain == null) return null;

        return RoutineLogDTO.builder()
                .id(domain.getId())
                .workoutLogId(domain.getWorkoutLogId())
                .routineId(domain.getRoutineId())
                .routineName(domain.getRoutineName())
                .startTime(domain.getStartTime())
                .endTime(domain.getEndTime())
                .notes(domain.getNotes())
                .completed(domain.getCompleted())
                .exerciseLogs(domain.getExerciseLogs() != null
                        ? domain.getExerciseLogs().stream()
                            .map(RoutineExerciseLogMapper::toDto)
                            .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }

    public static List<RoutineLog> toDomainList(List<RoutineLogEntity> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(RoutineLogMapper::toDomain)
                .collect(Collectors.toList());
    }

    public static List<RoutineLogDTO> toDtoList(List<RoutineLogEntity> entities) {
        if (entities == null) return Collections.emptyList();
        return entities.stream()
                .map(RoutineLogMapper::toDto)
                .collect(Collectors.toList());
    }
}
