package com.sigma.gym.mappers;

import com.sigma.gym.entity.MembershipTypeEntity;
import com.sigma.gym.model.MembershipType;
import com.sigma.gym.DTOs.MembershipTypeDTO;

public class MembershipTypeMapper {

    public static MembershipType toDomain(MembershipTypeEntity entity) {
        if (entity == null) return null;
        return MembershipType.builder()
                .id(entity.getId())
                .name(entity.getName())
                .allowedDaysPerWeek(entity.getAllowedDaysPerWeek())
                .durationInDays(entity.getDurationInDays())
                .isActive(entity.isActive())
                .build();
    }

    public static MembershipTypeEntity toEntity(MembershipType domain) {
        if (domain == null) return null;
        return MembershipTypeEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .allowedDaysPerWeek(domain.getAllowedDaysPerWeek())
                .durationInDays(domain.getDurationInDays())
                .isActive(domain.isActive())
                .build();
    }

    public static MembershipTypeDTO toDto(MembershipTypeEntity entity) {
        if (entity == null) return null;
        return MembershipTypeDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .allowedDaysPerWeek(entity.getAllowedDaysPerWeek())
                .durationInDays(entity.getDurationInDays())
                .isActive(entity.isActive())
                .build();
    }

    public static MembershipTypeEntity toEntityFromDto(MembershipTypeDTO dto) {
        if (dto == null) return null;
        return MembershipTypeEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .allowedDaysPerWeek(dto.getAllowedDaysPerWeek())
                .durationInDays(dto.getDurationInDays())
                .isActive(dto.isActive())
                .build();
    }
       
    // Domain → DTO conversion
    public static MembershipTypeDTO toDto(MembershipType domain) {
        if (domain == null) return null;
        return MembershipTypeDTO.builder()
                .id(domain.getId())
                .name(domain.getName())
                .allowedDaysPerWeek(domain.getAllowedDaysPerWeek())
                .durationInDays(domain.getDurationInDays())
                .isActive(domain.isActive())
                .build();
    }
    
    // DTO → Domain conversion
    public static MembershipType toDomain(MembershipTypeDTO dto) {
        if (dto == null) return null;
        return MembershipType.builder()
                .id(dto.getId())
                .name(dto.getName())
                .allowedDaysPerWeek(dto.getAllowedDaysPerWeek())
                .durationInDays(dto.getDurationInDays())
                .isActive(dto.isActive())
                .build();
    }
}
