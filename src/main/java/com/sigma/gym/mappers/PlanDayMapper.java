package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.PlanDayDTO;
import com.sigma.gym.entity.PlanDayEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for PlanDay entity and DTO conversions
 */
@Component
public class PlanDayMapper {

    public static PlanDayDTO toDTO(PlanDayEntity entity) {
        if (entity == null) {
            return null;
        }

        return PlanDayDTO.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .orderIndex(entity.getOrderIndex())
                .planId(entity.getPlanId())
                .build();
    }

    public static PlanDayEntity toEntity(PlanDayDTO dto) {
        if (dto == null) {
            return null;
        }

        return PlanDayEntity.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .orderIndex(dto.getOrderIndex())
                .build();
    }

    public static List<PlanDayDTO> toDTOList(List<PlanDayEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(PlanDayMapper::toDTO)
                .collect(Collectors.toList());
    }

    public static List<PlanDayEntity> toEntityList(List<PlanDayDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(PlanDayMapper::toEntity)
                .collect(Collectors.toList());
    }
}
