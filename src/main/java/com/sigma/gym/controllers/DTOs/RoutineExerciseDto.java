package com.sigma.gym.controllers.DTOs;


import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutineExerciseDto {

    private Long id;
    private Long routineId;
    private Long exerciseId;

    private String exerciseName; // útil para mostrar directo en el front
    private Boolean isWarmup;
    private Integer sets;
    private Integer reps;
    private Integer weight;
}
