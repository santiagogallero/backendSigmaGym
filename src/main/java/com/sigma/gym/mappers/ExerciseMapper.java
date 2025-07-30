package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.ExerciseDTO;
import com.sigma.gym.entity.ExerciseEntity;

public class ExerciseMapper {

    public static ExerciseDTO toDto(ExerciseEntity exercise) {
        if (exercise == null) return null;

        return ExerciseDTO.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .description(exercise.getDescription())
                .category(exercise.getCategory())
                .equipment(exercise.getEquipment())
                .duration(exercise.getDuration())
                .sets(exercise.getSets())
                .reps(exercise.getReps())
                .videoUrl(exercise.getVideoUrl())
                .build();
    }

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
                .build();
    }
}
