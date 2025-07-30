package com.sigma.gym.model;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlan {
    private Long id;
    private String name;
    private String goal;
    private String difficulty;
    private String notes;
    private String createdAt;
    private User trainer;
    private List<Routine> routines;
}
