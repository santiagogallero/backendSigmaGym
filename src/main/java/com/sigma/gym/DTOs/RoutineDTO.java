package com.sigma.gym.DTOs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutineDTO {
    private Long id;
    private String name;
    private String description;
    private Integer duration;
    private String difficulty;
    private String dayOfWeek;
    private Long workoutPlanId; // solo el ID, no el objeto entero
    private List<RoutineExerciseDTO> routineExercises; // lista de ejercicios de la rutina
}
