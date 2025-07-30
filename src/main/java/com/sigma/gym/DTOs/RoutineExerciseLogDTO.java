package com.sigma.gym.DTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoutineExerciseLogDTO {

    private Long id;
    private Long routineLogId;
    private Long exerciseId;
    private String exerciseName;
    private Long routineExerciseId;
    private Integer repsPerformed;
    private Integer setsPerformed;
    private BigDecimal weightUsed;
    private Integer duration;
    private String notes;
    private Boolean completed;
}
