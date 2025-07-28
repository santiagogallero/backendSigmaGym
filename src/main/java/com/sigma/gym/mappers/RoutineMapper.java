package com.sigma.gym.mappers;

import com.sigma.gym.controllers.DTOs.RoutineDTO;
import com.sigma.gym.entity.Routine;
import com.sigma.gym.entity.WorkoutPlan;

public class RoutineMapper {

    public static RoutineDTO toDto(Routine entity) {
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

    public static Routine toEntity(RoutineDTO dto, WorkoutPlan workoutPlan) {
        if (dto == null) return null;

        return Routine.builder()
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
