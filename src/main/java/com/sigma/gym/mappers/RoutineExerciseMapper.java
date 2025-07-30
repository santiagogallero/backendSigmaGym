package com.sigma.gym.mappers;



import com.sigma.gym.DTOs.RoutineExerciseDTO;
import com.sigma.gym.entity.RoutineExerciseEntity;

public class RoutineExerciseMapper {

    public static RoutineExerciseDTO toDto(RoutineExerciseEntity re) {
        if (re == null) return null;

        return RoutineExerciseDTO.builder()
                .id(re.getId())
                .routineId(re.getRoutine().getId())
                .exerciseId(re.getExercise().getId())
                .exerciseName(re.getExercise().getName()) // extra para mostrar sin otra query
                .isWarmup(re.getIsWarmup())
                .sets(re.getSets())
                .reps(re.getReps())
                .weight(re.getWeight())
                .build();
    }

    public static RoutineExerciseEntity toEntity(RoutineExerciseDTO dto) {
        if (dto == null) return null;

        // Tené en cuenta: acá necesitarías pasar las entidades Exercise y Routine reales
        // desde el service, porque no las podés construir solo con los IDs.
        RoutineExerciseEntity re = new RoutineExerciseEntity();
        re.setId(dto.getId());
        re.setIsWarmup(dto.getIsWarmup());
        re.setSets(dto.getSets());
        re.setReps(dto.getReps());
        re.setWeight(dto.getWeight());
        return re;
    }
}
