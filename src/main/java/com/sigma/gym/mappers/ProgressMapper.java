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
                .weight(null)         // ⚠️ opcional: completar si los agregás a Entity
                .muscleMass(null)
                .fatPercentage(null)
                .build();
    }

    public static ProgressEntity toEntity(Progress domain) {
        if (domain == null) return null;

        return ProgressEntity.builder()
                .id(domain.getId())
                .date(domain.getDate())
                .user(domain.getUserId() != null ? UserEntity.builder().id(domain.getUserId()).build() : null)
                .build();
    }

    public static ProgressDTO toDto(ProgressEntity entity) {
        if (entity == null) return null;

        return ProgressDTO.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .date(entity.getDate())
                .notes(entity.getNotes())
                .build();
    }

    public static ProgressEntity toEntity(ProgressDTO dto) {
        if (dto == null) return null;

        return ProgressEntity.builder()
                .id(dto.getId())
                .date(dto.getDate())
                .notes(dto.getNotes())
                .user(dto.getUserId() != null ? UserEntity.builder().id(dto.getUserId()).build() : null)
                .build();
    }
    public static ProgressDTO toDto(Progress domain) {
        if (domain == null) return null;

        return ProgressDTO.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .date(domain.getDate())
                .notes(domain.getNotes())
                .build();
    }
}

