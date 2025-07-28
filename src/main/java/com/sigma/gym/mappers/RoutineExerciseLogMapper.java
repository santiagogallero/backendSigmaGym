package com.sigma.gym.mappers;

import com.sigma.gym.controllers.DTOs.RoutineExerciseLogDto;
import com.sigma.gym.entity.RoutineExerciseLog;

import java.util.List;
import java.util.stream.Collectors;

public class RoutineExerciseLogMapper {

    public static RoutineExerciseLogDto toDto(RoutineExerciseLog entity) {
        if (entity == null) {
            return null;
        }

        return RoutineExerciseLogDto.builder()
                .id(entity.getId())
                .routineLogId(entity.getRoutineLog() != null ? entity.getRoutineLog().getId() : null)
                .exerciseId(entity.getExercise() != null ? entity.getExercise().getId() : null)
                .exerciseName(entity.getExercise() != null ? entity.getExercise().getName() : null)
                .routineExerciseId(entity.getRoutineExercise() != null ? entity.getRoutineExercise().getId() : null)
                .repsPerformed(entity.getRepsPerformed())
                .setsPerformed(entity.getSetsPerformed())
                .weightUsed(entity.getWeightUsed())
                .duration(entity.getDuration())
                .notes(entity.getNotes())
                .completed(entity.getCompleted())
                .build();
    }

    public static RoutineExerciseLog toEntity(RoutineExerciseLogDto dto) {
        if (dto == null) {
            return null;
        }

        return RoutineExerciseLog.builder()
                .id(dto.getId())
                .repsPerformed(dto.getRepsPerformed())
                .setsPerformed(dto.getSetsPerformed())
                .weightUsed(dto.getWeightUsed())
                .duration(dto.getDuration())
                .notes(dto.getNotes())
                .completed(dto.getCompleted())
                // IMPORTANTE: exercise, routineLog y routineExercise se deben setear aparte en el service
                .build();
    }

    public static List<RoutineExerciseLogDto> toDtoList(List<RoutineExerciseLog> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(RoutineExerciseLogMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<RoutineExerciseLog> toEntityList(List<RoutineExerciseLogDto> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream()
                .map(RoutineExerciseLogMapper::toEntity)
                .collect(Collectors.toList());
    }
}
