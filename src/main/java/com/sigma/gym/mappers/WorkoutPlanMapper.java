package com.sigma.gym.mappers;

import com.sigma.gym.entity.WorkoutPlanEntity;
import com.sigma.gym.model.WorkoutPlan;
import com.sigma.gym.model.User;

import java.util.stream.Collectors;

public class WorkoutPlanMapper {

    public static WorkoutPlan toDomain(WorkoutPlanEntity entity) {
        if (entity == null) return null;

        return WorkoutPlan.builder()
                .id(entity.getId())
                .name(entity.getName())
                .goal(entity.getGoal())
                .difficulty(entity.getDifficulty())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .trainer(entity.getTrainer() != null 
                    ? UserMapperEntity.toDto(entity.getTrainer()) 
                    : null)
                .routines(null) // ⚠️ opcional: podés mapear después con RoutineMapper
                .members(entity.getMembers() != null 
                    ? entity.getMembers().stream()
                        .map(UserMapperEntity::toDto)
                        .collect(Collectors.toList())
                    : null)
                .build();
    }

    public static WorkoutPlanEntity toEntity(WorkoutPlan model) {
        if (model == null) return null;

        return WorkoutPlanEntity.builder()
                .id(model.getId())
                .name(model.getName())
                .goal(model.getGoal())
                .difficulty(model.getDifficulty())
                .notes(model.getNotes())
                .createdAt(model.getCreatedAt())
                .trainer(null) // ⚠️ se asigna en el service según contexto
                .routines(null) // ⚠️ idem arriba, usar RoutineMapper si se necesita
                .members(null) // ⚠️ también, esto podés dejarlo vacío y asociar en el service
                .build();
    }
}
