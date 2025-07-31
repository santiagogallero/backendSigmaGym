package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.RoutineDTO;
import com.sigma.gym.entity.RoutineEntity;
import com.sigma.gym.entity.WorkoutPlanEntity;
import com.sigma.gym.model.Routine;

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
                .workoutPlan(workoutPlan)
                .build();
    }

    public static Routine toDomain(RoutineEntity entity) {
        if (entity == null) return null;

        return Routine.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .duration(entity.getDuration())
                .difficulty(entity.getDifficulty())
                .dayOfWeek(entity.getDayOfWeek())
                .workoutPlanId(entity.getWorkoutPlan() != null ? entity.getWorkoutPlan().getId() : null)
                .build();
    }
    public static RoutineEntity toEntity(Routine domain) {
        if (domain == null) return null;

        return RoutineEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .duration(domain.getDuration())
                .difficulty(domain.getDifficulty())
                .dayOfWeek(domain.getDayOfWeek())
                .workoutPlan(null) // This should be set later when associating with a WorkoutPlan
                .build();
    }
     // ✅ AGREGA ESTE MÉTODO que solo recibe DTO
    public static RoutineEntity toEntity(RoutineDTO dto) {
        if (dto == null) return null;

        return RoutineEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .duration(dto.getDuration())
                .difficulty(dto.getDifficulty())
                .dayOfWeek(dto.getDayOfWeek())
                .workoutPlan(null) // Se establecerá después en el contexto del WorkoutPlan
                .build();
    }   
        // Add this method: Routine (domain) → RoutineDTO
        public static RoutineDTO toDto(Routine domain) {
            if (domain == null) return null;
            return RoutineDTO.builder()
                    .id(domain.getId())
                    .name(domain.getName())
                    .description(domain.getDescription())
                    .duration(domain.getDuration())
                    .difficulty(domain.getDifficulty())
                    .dayOfWeek(domain.getDayOfWeek())
                    // Map other fields as needed
                    .build();
        }
     
        
        // Add this method: RoutineDTO → Routine (domain)
        public static Routine toDomain(RoutineDTO dto) {
            if (dto == null) return null;
            return Routine.builder()
                    .id(dto.getId())
                    .name(dto.getName())
                    .description(dto.getDescription())
                    .duration(dto.getDuration())
                    .difficulty(dto.getDifficulty())
                    .dayOfWeek(dto.getDayOfWeek())
                    // Map other fields as needed
                    .build();
        }
        
    
}
