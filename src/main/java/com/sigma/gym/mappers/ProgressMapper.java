package com.sigma.gym.mappers;

import com.sigma.gym.entity.ProgressEntity;
import com.sigma.gym.model.Progress;

import com.sigma.gym.DTOs.ProgressDTO;
import com.sigma.gym.entity.UserEntity;

public class ProgressMapper {

    public static Progress toDomain(ProgressEntity entity) {
        if (entity == null) return null;

        return Progress.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .date(entity.getDate())
                .weight(entity.getWeight())         // ⚠️ opcional: completar si los agregás a Entity
                .muscleMass(entity.getMuscleMass()) // ⚠️ opcional: completar si los agregás a Entity
                .notes(entity.getNotes())
                .bodyFatPercentage(entity.getBodyFatPercentage())
                .build();
    }
public static ProgressEntity toEntity(Progress domain) {
    if (domain == null) return null;

    ProgressEntity entity = new ProgressEntity();
    entity.setId(domain.getId());
    entity.setDate(domain.getDate());
    entity.setWeight(domain.getWeight());
    entity.setMuscleMass(domain.getMuscleMass());
    entity.setBodyFatPercentage(domain.getBodyFatPercentage());
    entity.setNotes(domain.getNotes());

    if (domain.getUserId() != null) {
        UserEntity user = new UserEntity();
        user.setId(domain.getUserId());
        entity.setUser(user);
    }

    return entity;
}


    public static ProgressDTO toDto(ProgressEntity entity) {
        if (entity == null) return null;

        return ProgressDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .date(entity.getDate())
                .notes(entity.getNotes())
                .weight(entity.getWeight())
                .muscleMass(entity.getMuscleMass() != null ? entity.getMuscleMass().intValue() : null) // Convert Double to Integer if needed
                .bodyFatPercentage(entity.getBodyFatPercentage() != null ? entity.getBodyFatPercentage().intValue() : null)
                .build();
    }

public static ProgressEntity toEntity(ProgressDTO dto) {
    if (dto == null) return null;

    ProgressEntity entity = new ProgressEntity();
    entity.setId(dto.getId());
    entity.setDate(dto.getDate());
    entity.setNotes(dto.getNotes());
    entity.setWeight(dto.getWeight());
    entity.setMuscleMass(dto.getMuscleMass() != null ? dto.getMuscleMass().doubleValue() : null);
    entity.setBodyFatPercentage(dto.getBodyFatPercentage() != null ? dto.getBodyFatPercentage().doubleValue() : null);

    if (dto.getUserId() != null) {
        UserEntity user = new UserEntity();
        user.setId(dto.getUserId());
        entity.setUser(user);
    }

    return entity;
}

    public static ProgressDTO toDto(Progress domain) {
        if (domain == null) return null;

        return ProgressDTO.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .date(domain.getDate())
                .notes(domain.getNotes())
                .weight(domain.getWeight())
                .muscleMass(domain.getMuscleMass() != null ? domain.getMuscleMass().intValue() : null) // Convert Double to Integer if needed
                .bodyFatPercentage(domain.getBodyFatPercentage() != null ? domain.getBodyFatPercentage().intValue() : null)
                .build();
    }
}

