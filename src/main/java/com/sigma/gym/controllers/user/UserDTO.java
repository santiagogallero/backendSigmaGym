package com.sigma.gym.controllers.user;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.sigma.gym.entities.Progress;
import com.sigma.gym.entities.Role;
import com.sigma.gym.entities.User;
import com.sigma.gym.entities.WorkoutPlan;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    @NotNull
    private String username;
    @NotNull
    private String firstName; 
    @NotNull
    private String lastName;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private Role role;

    @JsonIgnore
    private LocalDate startDate; // Fecha de alta
    @JsonIgnore
    private LocalDate lastVisitDate; 
    @JsonIgnore
    private String membershipType; // Mensual, Trimestral, Premium, etc.
    @JsonIgnore
    private LocalDate membershipExpirationDate; // Fecha de vencimiento de la membresía
    @JsonIgnore
    private Boolean isActive; // ¿Está al día?
    @JsonIgnore
    private List<WorkoutPlan> workoutPlans;  // planes de entrenamiento asignados
    @JsonIgnore
    private List<Progress> progressHistory;  // peso levantado, tiempos, fotos, etc.

    public User toEntity() {
        return new User(
            this.id,
            this.username,
            this.firstName,
            this.lastName,
            this.email,
            this.password,
            this.role,
            null, // orders field removed as it doesn't exist in DTO
            this.startDate,
            this.lastVisitDate,
            this.membershipType,
            this.membershipExpirationDate,
            this.isActive,
            this.workoutPlans,
            this.progressHistory
        );
    }
}