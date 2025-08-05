package com.sigma.gym.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineLog {
    private Long id;
    private Long userId;
    private Long workoutLogId;
    private Long routineId;
    private String routineName; // ✅ agregado para evitar error
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String notes;
    private Boolean completed; // opcional, por si lo usás
    private List<RoutineExerciseLog> exerciseLogs; // para mantener la relación
}
