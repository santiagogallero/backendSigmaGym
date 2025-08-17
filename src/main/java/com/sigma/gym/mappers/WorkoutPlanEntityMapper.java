package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.WorkoutPlanDTO;
import com.sigma.gym.entity.WorkoutPlanEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Enhanced mapper for WorkoutPlan entity and DTO conversions with new fields
 */
@Component
public class WorkoutPlanEntityMapper {

    public static WorkoutPlanDTO toDTO(WorkoutPlanEntity entity) {
        if (entity == null) {
            return null;
        }

        return WorkoutPlanDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .status(entity.getStatus())
                .goal(entity.getGoal())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .difficulty(entity.getDifficulty())
                .notes(entity.getNotes())
                .ownerId(entity.getOwnerId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                // Enhanced fields
                .planDays(PlanDayMapper.toDTOList(entity.getPlanDays()))
                .totalDays(entity.getTotalDays())
                .hasMembers(entity.hasMembers())
                // Legacy compatibility
                .trainer(null) // Would need UserMapper if needed
                .routines(null) // DEPRECATED
                .members(null) // Would need UserMapper if needed
                .build();
    }

    public static WorkoutPlanEntity toEntity(WorkoutPlanDTO dto) {
        if (dto == null) {
            return null;
        }

        return WorkoutPlanEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .slug(dto.getSlug())
                .status(dto.getStatus())
                .goal(dto.getGoal())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .difficulty(dto.getDifficulty())
                .notes(dto.getNotes())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .version(dto.getVersion())
                // Note: planDays, owner, etc. should be set separately
                .build();
    }

    /**
     * Convert to DTO with minimal fields (for list views)
     */
    public static WorkoutPlanDTO toSummaryDTO(WorkoutPlanEntity entity) {
        if (entity == null) {
            return null;
        }

        return WorkoutPlanDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .status(entity.getStatus())
                .goal(entity.getGoal())
                .difficulty(entity.getDifficulty())
                .ownerId(entity.getOwnerId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .totalDays(entity.getTotalDays())
                .hasMembers(entity.hasMembers())
                .build();
    }

    public static List<WorkoutPlanDTO> toDTOList(List<WorkoutPlanEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(WorkoutPlanEntityMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<WorkoutPlanDTO> toSummaryDTOList(List<WorkoutPlanEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(WorkoutPlanEntityMapper::toSummaryDTO)
                .collect(Collectors.toList());
    }

    public static List<WorkoutPlanEntity> toEntityList(List<WorkoutPlanDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(WorkoutPlanEntityMapper::toEntity)
                .collect(Collectors.toList());
    }
}
