package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.RoutineDTO;
import com.sigma.gym.entity.RoutineEntity;
import com.sigma.gym.entity.WorkoutPlanEntity;

public class RoutineMapper {

    public static RoutineDTO toDto(RoutineEntity entity) {
        if (entity == null) return null;

        return RoutineDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .duration(entity.getDuration())
                .difficulty(entity.getDifficulty())
                .dayOfWeek(entity.getDayOfWeek())
                .workoutPlanId(entity.getWorkoutPlan() != null ? entity.getWorkoutPlan().getId() : null)
                .build();
    }

    public static RoutineEntity toEntity(RoutineDTO dto, WorkoutPlanEntity workoutPlan) {
        if (dto == null) return null;

        return RoutineEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .duration(dto.getDuration())
                .difficulty(dto.getDifficulty())
                .dayOfWeek(dto.getDayOfWeek())
                .workoutPlan(workoutPlan) // ya tiene que estar cargado en el service
                .build();
    }
}
