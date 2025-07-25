package com.sigma.gym.mappers;

import com.sigma.gym.controllers.DTOs.RoutineLogDto;
import com.sigma.gym.entity.RoutineLog;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RoutineLogMapper {

    public static RoutineLogDto toDto(RoutineLog entity) {
        if (entity == null) {
            return null;
        }

        return RoutineLogDto.builder()
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

    public static RoutineLog toEntity(RoutineLogDto dto) {
    if (dto == null) return null;

    RoutineLog log = RoutineLog.builder()
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

    public static List<RoutineLogDto> toDtoList(List<RoutineLog> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(RoutineLogMapper::toDto)
                .collect(Collectors.toList());
    }
}