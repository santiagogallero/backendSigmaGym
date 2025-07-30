package com.sigma.gym.mappers;

import com.sigma.gym.entity.ProgressEntity;
import com.sigma.gym.model.Progress;

public class ProgressMapper {

    public static Progress toDomain(ProgressEntity entity) {
        if (entity == null) return null;
        return Progress.builder()
                .id(entity.getId())
                .date(entity.getDate())
                .build();
    }

    public static ProgressEntity toEntity(Progress domain) {
        if (domain == null) return null;
        return ProgressEntity.builder()
                .id(domain.getId())
                .date(domain.getDate())
                .build();
    }
}
