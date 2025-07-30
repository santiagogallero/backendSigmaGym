package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.RoutineExerciseLogDTO;
import com.sigma.gym.entity.RoutineExerciseLogEntity;

import java.util.List;
import java.util.stream.Collectors;

public class RoutineExerciseLogMapper {

    public static RoutineExerciseLogDTO toDto(RoutineExerciseLogEntity entity) {
        if (entity == null) {
            return null;
        }

        return RoutineExerciseLogDTO.builder()
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

    public static RoutineExerciseLogEntity toEntity(RoutineExerciseLogDTO dto) {
        if (dto == null) {
            return null;
        }

        return RoutineExerciseLogEntity.builder()
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

    public static List<RoutineExerciseLogDTO> toDtoList(List<RoutineExerciseLogEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(RoutineExerciseLogMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<RoutineExerciseLogEntity> toEntityList(List<RoutineExerciseLogDTO> dtos) {
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream()
                .map(RoutineExerciseLogMapper::toEntity)
                .collect(Collectors.toList());
    }
}
