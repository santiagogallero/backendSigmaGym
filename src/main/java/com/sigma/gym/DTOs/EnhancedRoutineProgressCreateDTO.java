package com.sigma.gym.DTOs;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedRoutineProgressCreateDTO {
    
    @NotNull(message = "El ID de la rutina es obligatorio")
    private Long routineId;
    
    private LocalDate targetEndDate;
    
    @NotNull(message = "El total de días planificados es obligatorio")
    @Min(value = 1, message = "El total de días planificados debe ser mayor a 0")
    private Integer totalDaysPlanned;
    
    @Min(value = 1, message = "La dificultad percibida debe estar entre 1 y 10")
    @Max(value = 10, message = "La dificultad percibida debe estar entre 1 y 10")
    private Integer perceivedDifficulty;
    
    @Size(max = 1000, message = "Las notas del usuario no pueden exceder 1000 caracteres")
    private String userNotes;
    
    private LocalDateTime nextScheduledSession;
}
