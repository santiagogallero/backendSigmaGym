package com.sigma.gym.controllers.DTOs;



import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseDto {

    private Long id;
    private String name;
    private String description;
    private String category;     // e.g., Cardio, Strength
    private String equipment;    // e.g., Barbell, Bodyweight
    private Integer duration;    // en segundos
    private Integer sets;        // sugerido base (opcional)
    private Integer reps;        // sugerido base (opcional)
    private String videoUrl;
}
