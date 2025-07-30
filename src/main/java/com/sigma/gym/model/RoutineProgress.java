package com.sigma.gym.model;

import java.time.LocalDate;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineProgress {
    private Long id;
    private Long userId;
    private Long routineId;
    private int completedDays;
    private LocalDate startDate;
    private LocalDate lastUpdate;
    private boolean isCompleted;
    private Long progressId;
}
