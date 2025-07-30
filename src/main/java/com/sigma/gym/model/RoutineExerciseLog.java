package com.sigma.gym.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineExerciseLog {
    private Long id;
    private Long workoutLogId;
    private Long exerciseId;
    private Integer setsCompleted;
    private Integer repsCompleted;
}
