package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.RoutineExerciseLogDTO;
import com.sigma.gym.entity.RoutineExerciseLogEntity;
import com.sigma.gym.model.RoutineExerciseLog;

import java.util.List;
import java.util.stream.Collectors;

public class RoutineExerciseLogMapper {

    // Entity → DTO
    public static RoutineExerciseLogDTO toDto(RoutineExerciseLogEntity entity) {
        if (entity == null) return null;

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
                .isWarmup(entity.getIsWarmup())
                .setsCompleted(entity.getSetsCompleted())
                .repsCompleted(entity.getRepsCompleted())
                .build();
    }

    // DTO → Entity
    public static RoutineExerciseLogEntity toEntity(RoutineExerciseLogDTO dto) {
        if (dto == null) return null;

        return RoutineExerciseLogEntity.builder()
                .id(dto.getId())
                .repsPerformed(dto.getRepsPerformed())
                .setsPerformed(dto.getSetsPerformed())
                .weightUsed(dto.getWeightUsed())
                .duration(dto.getDuration())
                .notes(dto.getNotes())
                .completed(dto.getCompleted())
                .isWarmup(dto.getIsWarmup())
                .setsCompleted(dto.getSetsCompleted())
                .repsCompleted(dto.getRepsCompleted())
                // IMPORTANTE: exercise, routineLog y routineExercise se asignan en el service
                .build();
    }

    // Entity → Domain
    public static RoutineExerciseLog toDomain(RoutineExerciseLogEntity entity) {
        if (entity == null) return null;

        return RoutineExerciseLog.builder()
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
                .isWarmup(entity.getIsWarmup())
                .setsCompleted(entity.getSetsCompleted())
                .repsCompleted(entity.getRepsCompleted())
                .build();
    }

    // Domain → Entity
    public static RoutineExerciseLogEntity toEntity(RoutineExerciseLog domain) {
        if (domain == null) return null;

        return RoutineExerciseLogEntity.builder()
                .id(domain.getId())
                .repsPerformed(domain.getRepsPerformed())
                .setsPerformed(domain.getSetsPerformed())
                .weightUsed(domain.getWeightUsed())
                .duration(domain.getDuration())
                .notes(domain.getNotes())
                .completed(domain.getCompleted())
                .isWarmup(domain.getIsWarmup())
                .setsCompleted(domain.getSetsCompleted())
                .repsCompleted(domain.getRepsCompleted())
                // IMPORTANTE: exercise, routineLog y routineExercise se asignan en el service
                .build();
    }
    // Domain → DTO
    public static RoutineExerciseLogDTO toDto(RoutineExerciseLog domain) {
    if (domain == null) return null;

    return RoutineExerciseLogDTO.builder()
            .id(domain.getId())
            .routineLogId(domain.getRoutineLogId())
            .exerciseId(domain.getExerciseId())
            .exerciseName(domain.getExerciseName())
            .routineExerciseId(domain.getRoutineExerciseId())
            .repsPerformed(domain.getRepsPerformed())
            .setsPerformed(domain.getSetsPerformed())
            .weightUsed(domain.getWeightUsed())
            .duration(domain.getDuration())
            .notes(domain.getNotes())
            .completed(domain.getCompleted())
            .isWarmup(domain.getIsWarmup())
            .setsCompleted(domain.getSetsCompleted())
            .repsCompleted(domain.getRepsCompleted())
            .build();
    }


    // Listas
    public static List<RoutineExerciseLogDTO> toDtoList(List<RoutineExerciseLogEntity> entities) {
        return entities == null ? List.of() :
                entities.stream().map(RoutineExerciseLogMapper::toDto).collect(Collectors.toList());
    }

    public static List<RoutineExerciseLogEntity> toEntityList(List<RoutineExerciseLogDTO> dtos) {
        return dtos == null ? List.of() :
                dtos.stream().map(RoutineExerciseLogMapper::toEntity).collect(Collectors.toList());
    }

    public static List<RoutineExerciseLog> toDomainList(List<RoutineExerciseLogEntity> entities) {
        return entities == null ? List.of() :
                entities.stream().map(RoutineExerciseLogMapper::toDomain).collect(Collectors.toList());
    }
}
