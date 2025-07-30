package com.sigma.gym.DTOs;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkOutLogDTO {

    private Long id;
    private Long userId;
    private Long workoutPlanId;
    private String workoutPlanName;
    private LocalDate date;
    private String notes;
   private List<RoutineLogDTO> routineLogs;
}