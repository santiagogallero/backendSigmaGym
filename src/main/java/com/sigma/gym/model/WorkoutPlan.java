package com.sigma.gym.model;

import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlan {
    
    private Long id;
    
    private String name;
    
    private String goal;
    
    private String description;
    
    // ✅ CORREGIDO: Usar LocalDate consistentemente
    private LocalDate startDate;
    
    private LocalDate endDate;
    
    private String difficulty;
    
    private String notes;
    
    private LocalDate createdAt;
    
    private User trainer;
    
    // ✅ CORREGIDO: Inicializar listas para evitar NPE
    @Builder.Default
    private List<Routine> routines = new ArrayList<>();
    
    @Builder.Default
    private List<User> members = new ArrayList<>();

}
