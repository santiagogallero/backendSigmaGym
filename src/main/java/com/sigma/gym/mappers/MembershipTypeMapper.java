package com.sigma.gym.mappers;

import com.sigma.gym.entity.MembershipTypeEntity;
import com.sigma.gym.model.MembershipType;

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
}
