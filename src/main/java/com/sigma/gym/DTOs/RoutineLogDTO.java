package com.sigma.gym.DTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutineLogDTO {

    private Long id;
    private Long workoutLogId;
    private Long routineId;
    private String routineName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String notes;
    private List<RoutineExerciseLogDTO> exerciseLogs;
}