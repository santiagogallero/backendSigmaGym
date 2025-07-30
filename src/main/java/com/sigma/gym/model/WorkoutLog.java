package com.sigma.gym.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLog {
    private Long id;
    private Long userId;
    private LocalDateTime date;
    private List<RoutineExerciseLog> routineExerciseLogs;
}
