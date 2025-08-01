package com.sigma.gym.DTOs;

import com.sigma.gym.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseDTO {

    private Long id;
    private String name;
    private String description;
    private String category;     // e.g., Cardio, Strength
    private String equipment;    // e.g., Barbell, Bodyweight
    private Integer duration;    // en segundos
    private Integer sets;        // sugerido base (opcional)
    private Integer reps;        // sugerido base (opcional)
    private String videoUrl;
    private User createdBy; // Usuario que creó el ejercicio
}
 
