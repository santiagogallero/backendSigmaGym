package com.sigma.gym.mappers;

import com.sigma.gym.controllers.DTOs.ExerciseDto;
import com.sigma.gym.entity.Exercise;

public class ExerciseMapper {

    public static ExerciseDto toDto(Exercise exercise) {
        if (exercise == null) return null;

        return ExerciseDto.builder()
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

    public static Exercise toEntity(ExerciseDto dto) {
        if (dto == null) return null;

        return Exercise.builder()
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
