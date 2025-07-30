package com.sigma.gym.model;

import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoutineLog {
    private Long id;
    private Long userId;
    private Long routineId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
