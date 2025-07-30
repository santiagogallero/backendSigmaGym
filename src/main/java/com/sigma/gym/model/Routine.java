package com.sigma.gym.model;

import java.util.List;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Routine {
    private Long id;
    private String name;
    private String description;
    private Integer duration;
    private String difficulty;
    private String dayOfWeek;
    private Long workoutPlanId;
    private List<RoutineExercise> routineExercises;
}
