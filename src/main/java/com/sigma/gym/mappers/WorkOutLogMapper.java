package com.sigma.gym.mappers;

import com.sigma.gym.controllers.DTOs.WorkOutLogDto;
import com.sigma.gym.entity.WorkOutLog;

import java.util.List;
import java.util.stream.Collectors;

public class WorkOutLogMapper {

    public static WorkOutLogDto toDto(WorkOutLog entity) {
        if (entity == null) {
            return null;
        }

        return WorkOutLogDto.builder()
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

    public static WorkOutLog toEntity(WorkOutLogDto dto) {
        if (dto == null) {
            return null;
        }

        return WorkOutLog.builder()
                .id(dto.getId())
                .date(dto.getDate())
                .notes(dto.getNotes())
                .build();
    }

    public static List<WorkOutLogDto> toDtoList(List<WorkOutLog> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(WorkOutLogMapper::toDto)
                .collect(Collectors.toList());
    }
}