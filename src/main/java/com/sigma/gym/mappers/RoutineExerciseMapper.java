package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.RoutineExerciseDTO;
import com.sigma.gym.entity.ExerciseEntity;
import com.sigma.gym.entity.RoutineEntity;
import com.sigma.gym.entity.RoutineExerciseEntity;
import com.sigma.gym.model.RoutineExercise;

import java.util.List;
import java.util.stream.Collectors;

public class RoutineExerciseMapper {

    public static RoutineExerciseDTO toDto(RoutineExercise model) {
        if (model == null) return null;

        return RoutineExerciseDTO.builder()
                .id(model.getId())
                .routineId(model.getRoutineId())
                .exerciseId(model.getExerciseId())
                .exerciseName(model.getExerciseName())
                .isWarmup(model.getIsWarmup())
                .sets(model.getSets())
                .reps(model.getReps())
                .weight(model.getWeight())
                .build();
    }

    public static RoutineExercise toModel(RoutineExerciseDTO dto) {
        if (dto == null) return null;

        RoutineExercise model = new RoutineExercise();
        model.setId(dto.getId());
        model.setRoutineId(dto.getRoutineId());
        model.setExerciseId(dto.getExerciseId());
        model.setExerciseName(dto.getExerciseName());
        model.setIsWarmup(dto.getIsWarmup());
        model.setSets(dto.getSets());
        model.setReps(dto.getReps());
        model.setWeight(dto.getWeight());
        return model;
    }

    public static RoutineExercise toModel(RoutineExerciseEntity entity) {
        if (entity == null) return null;

        RoutineExercise model = new RoutineExercise();
        model.setId(entity.getId());
        model.setRoutineId(entity.getRoutine().getId());
        model.setExerciseId(entity.getExercise().getId());
        model.setExerciseName(entity.getExercise().getName());
        model.setIsWarmup(entity.getIsWarmup());
        model.setSets(entity.getSets());
        model.setReps(entity.getReps());
        model.setWeight(entity.getWeight());
        return model;
    }

    public static RoutineExerciseEntity toEntity(RoutineExercise model) {
        if (model == null) return null;

        RoutineExerciseEntity entity = new RoutineExerciseEntity();
        entity.setId(model.getId());

        RoutineEntity routine = new RoutineEntity();
        routine.setId(model.getRoutineId());
        entity.setRoutine(routine);

        ExerciseEntity exercise = new ExerciseEntity();
        exercise.setId(model.getExerciseId());
        entity.setExercise(exercise);

        entity.setIsWarmup(model.getIsWarmup());
        entity.setSets(model.getSets());
        entity.setReps(model.getReps());
        entity.setWeight(model.getWeight());
        return entity;
    }

    public static RoutineExerciseDTO toDto(RoutineExerciseEntity re) {
        if (re == null) return null;

        return RoutineExerciseDTO.builder()
                .id(re.getId())
                .routineId(re.getRoutine().getId())
                .exerciseId(re.getExercise().getId())
                .exerciseName(re.getExercise().getName())
                .isWarmup(re.getIsWarmup())
                .sets(re.getSets())
                .reps(re.getReps())
                .weight(re.getWeight())
                .build();
    }

    public static RoutineExerciseEntity toEntity(RoutineExerciseDTO dto) {
        if (dto == null) return null;

        RoutineExerciseEntity re = new RoutineExerciseEntity();
        re.setId(dto.getId());

        ExerciseEntity exercise = new ExerciseEntity();
        exercise.setId(dto.getExerciseId());
        re.setExercise(exercise);

        RoutineEntity routine = new RoutineEntity();
        routine.setId(dto.getRoutineId());
        re.setRoutine(routine);

        re.setIsWarmup(dto.getIsWarmup());
        re.setSets(dto.getSets());
        re.setReps(dto.getReps());
        re.setWeight(dto.getWeight());

        return re;
    }

    public static List<RoutineExerciseDTO> toDtoList(List<RoutineExerciseEntity> entities) {
        if (entities == null) return null;

        return entities.stream()
                .map(RoutineExerciseMapper::toDto)
                .collect(Collectors.toList());
    }

    public static List<RoutineExerciseEntity> toEntityList(List<RoutineExerciseDTO> dtos) {
        if (dtos == null) return null;

        return dtos.stream()
                .map(RoutineExerciseMapper::toEntity)
                .collect(Collectors.toList());
    }

    public static List<RoutineExercise> toModelList(List<RoutineExerciseEntity> entities) {
        if (entities == null) return null;

        return entities.stream()
                .map(RoutineExerciseMapper::toModel)
                .collect(Collectors.toList());
    }

    public static List<RoutineExerciseEntity> toEntityListFromModel(List<RoutineExercise> models) {
        if (models == null) return null;

        return models.stream()
                .map(RoutineExerciseMapper::toEntity)
                .collect(Collectors.toList());
    }
   public static List<RoutineExercise> toDomainListFromDto(List<RoutineExerciseDTO> dtos) {
    if (dtos == null) return null;

    return dtos.stream()
            .map(RoutineExerciseMapper::toModel) // o toDomain, depende el nombre
            .collect(Collectors.toList());
}
public static List<RoutineExerciseDTO> toDtoListFromDomain(List<RoutineExercise> domainList) {
    if (domainList == null) return null;

    return domainList.stream()
            .map(RoutineExerciseMapper::toDto)
            .collect(Collectors.toList());
}
    public static List<RoutineExercise> toDomainList(List<RoutineExerciseEntity> entities) {
        if (entities == null) return null;

        return entities.stream()
                .map(RoutineExerciseMapper::toModel)
                .collect(Collectors.toList());
    }




}