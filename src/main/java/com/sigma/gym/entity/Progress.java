package com.sigma.gym.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Progress {
    private Long id;
    private User user;
    private WorkoutPlan workoutPlan;
    private LocalDate date;
    private String notes;
    private List<RoutineProgress> routineProgressList;
}
