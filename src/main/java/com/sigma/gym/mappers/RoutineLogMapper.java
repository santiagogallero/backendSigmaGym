package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.RoutineLogDTO;
import com.sigma.gym.entity.RoutineLogEntity;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RoutineLogMapper {

    public static RoutineLogDTO toDto(RoutineLogEntity entity) {
        if (entity == null) {
            return null;
        }

        return RoutineLogDTO.builder()
                .id(entity.getId())
                .workoutLogId(entity.getWorkoutLog() != null ? entity.getWorkoutLog().getId() : null)
                .routineId(entity.getRoutine() != null ? entity.getRoutine().getId() : null)
                .routineName(entity.getRoutine() != null ? entity.getRoutine().getName() : null)
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .notes(entity.getNotes())
                .exerciseLogs(entity.getExerciseLogs() != null ?
    entity.getExerciseLogs().stream()
        .map(RoutineExerciseLogMapper::toDto)
        .collect(Collectors.toList())
    : Collections.emptyList())
                .build();
    }

    public static RoutineLogEntity toEntity(RoutineLogDTO dto) {
    if (dto == null) return null;

    RoutineLogEntity log = RoutineLogEntity.builder()
        .id(dto.getId())
        .startTime(dto.getStartTime())
        .endTime(dto.getEndTime())
        .notes(dto.getNotes())
        .build();

    // En el servicio deberías hacer:
    // log.setRoutine(routineFromDb);
    // log.setWorkoutLog(workoutLogFromDb);

    return log;
}

    public static List<RoutineLogDTO> toDtoList(List<RoutineLogEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(RoutineLogMapper::toDto)
                .collect(Collectors.toList());
    }
}