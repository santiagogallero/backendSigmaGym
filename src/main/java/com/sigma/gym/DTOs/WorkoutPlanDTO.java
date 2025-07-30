package com.sigma.gym.DTOs;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

import com.sigma.gym.controllers.user.UserDTO;
import com.sigma.gym.DTOs.RoutineDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorkoutPlanDTO {
    private Long id;
    private String name;
    private String goal;
    private String difficulty;
    private String notes;
    private LocalDate createdAt;

    private UserDTO trainer; // Quien lo creó

    private List<RoutineDTO> routines; // Rutinas asociadas

    private List<UserDTO> members; // Usuarios que siguen el plan
}
