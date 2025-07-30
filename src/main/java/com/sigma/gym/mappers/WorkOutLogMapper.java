package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.WorkOutLogDTO;
import com.sigma.gym.entity.WorkOutLogEntity;

import java.util.List;
import java.util.stream.Collectors;

public class WorkOutLogMapper {

    public static WorkOutLogDTO toDto(WorkOutLogEntity entity) {
        if (entity == null) {
            return null;
        }

        return WorkOutLogDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .workoutPlanId(entity.getWorkoutPlan() != null ? entity.getWorkoutPlan().getId() : null)
                .workoutPlanName(entity.getWorkoutPlan() != null ? entity.getWorkoutPlan().getName() : null)
                .date(entity.getDate())
                .notes(entity.getNotes())
                .routineLogs(entity.getRoutineLogs() != null ? 
                    entity.getRoutineLogs().stream()
                            .map(RoutineLogMapper::toDto)
                            .collect(Collectors.toList()) : null)
                .build();
    }

    public static WorkOutLogEntity toEntity(WorkOutLogDTO dto) {
        if (dto == null) {
            return null;
        }

        return WorkOutLogEntity.builder()
                .id(dto.getId())
                .date(dto.getDate())
                .notes(dto.getNotes())
                .build();
    }

    public static List<WorkOutLogDTO> toDtoList(List<WorkOutLogEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(WorkOutLogMapper::toDto)
                .collect(Collectors.toList());
    }
}