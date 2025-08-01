package com.sigma.gym.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineExercise {
    private Long id;
    private Long routineId;
    private Long exerciseId;
    private Integer sets;
    private Integer reps;
    private Integer weight;
    private Boolean isWarmup;
    private String exerciseName; // útil para mostrar directo en el front
}
