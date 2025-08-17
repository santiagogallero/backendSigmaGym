package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.PlanExerciseDTO;
import com.sigma.gym.entity.PlanExerciseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for PlanExercise entity and DTO conversions
 */
@Component
public class PlanExerciseMapper {

    public static PlanExerciseDTO toDTO(PlanExerciseEntity entity) {
        if (entity == null) {
            return null;
        }

        return PlanExerciseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .reps(entity.getReps())
                .sets(entity.getSets())
                .weight(entity.getWeight())
                .weightUnit(entity.getWeightUnit())
                .restTimeSeconds(entity.getRestTimeSeconds())
                .notes(entity.getNotes())
                .isWarmup(entity.getIsWarmup())
                .orderIndex(entity.getOrderIndex())
                .dayId(entity.getDayId())
                .build();
    }

    public static PlanExerciseEntity toEntity(PlanExerciseDTO dto) {
        if (dto == null) {
            return null;
        }

        return PlanExerciseEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .reps(dto.getReps())
                .sets(dto.getSets())
                .weight(dto.getWeight())
                .weightUnit(dto.getWeightUnit())
                .restTimeSeconds(dto.getRestTimeSeconds())
                .notes(dto.getNotes())
                .isWarmup(dto.getIsWarmup())
                .orderIndex(dto.getOrderIndex())
                .build();
    }

    public static List<PlanExerciseDTO> toDTOList(List<PlanExerciseEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(PlanExerciseMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<PlanExerciseEntity> toEntityList(List<PlanExerciseDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(PlanExerciseMapper::toEntity)
                .collect(Collectors.toList());
    }
}
