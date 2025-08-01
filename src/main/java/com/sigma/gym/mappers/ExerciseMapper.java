package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.ExerciseDTO;
import com.sigma.gym.entity.ExerciseEntity;
import com.sigma.gym.model.Exercise;

public class ExerciseMapper {

    /**
     * Converts ExerciseEntity to ExerciseDTO
     */
    public static ExerciseDTO toDto(ExerciseEntity entity) {
        if (entity == null) return null;

        return ExerciseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .equipment(entity.getEquipment())
                .duration(entity.getDuration())
                .sets(entity.getSets())
                .reps(entity.getReps())
                .videoUrl(entity.getVideoUrl())
                .createdBy(entity.getCreatedBy() != null 
                    ? UserMapper.toDomain(entity.getCreatedBy()) // Convert UserEntity → User
                    : null)
                .build();
    }

    /**
     * Converts ExerciseDTO to ExerciseEntity
     */
    public static ExerciseEntity toEntity(ExerciseDTO dto) {
        if (dto == null) return null;

        return ExerciseEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .equipment(dto.getEquipment())
                .duration(dto.getDuration())
                .sets(dto.getSets())
                .reps(dto.getReps())
                .videoUrl(dto.getVideoUrl())
                .createdBy(dto.getCreatedBy() != null 
                    ? UserMapper.toEntity(dto.getCreatedBy()) // Convert UserDTO → UserEntity
                    : null)
                .build();
    }

    /**
     * Converts Exercise domain model to ExerciseDTO
     */
    public static ExerciseDTO toDto(Exercise domain) {
        if (domain == null) return null;

        return ExerciseDTO.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                .category(domain.getCategory())
                .equipment(domain.getEquipment())
                .duration(domain.getDuration())
                .sets(domain.getSets())
                .reps(domain.getReps())
                .videoUrl(domain.getVideoUrl())
                .createdBy(domain.getCreatedBy() != null 
                    ? domain.getCreatedBy() // User is already the correct type
                    : null)
                .build();
    }

    /**
     * Converts ExerciseEntity to Exercise domain model
     */
    public static Exercise toDomain(ExerciseEntity entity) {
        if (entity == null) return null;

        return Exercise.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .category(entity.getCategory())
                .equipment(entity.getEquipment())
                .duration(entity.getDuration())
                .sets(entity.getSets())
                .reps(entity.getReps())
                .videoUrl(entity.getVideoUrl())
                .createdBy(entity.getCreatedBy() != null 
                    ? UserMapper.toDomain(entity.getCreatedBy()) // Convert UserEntity → User
                    : null)
                .build();
    }
}
