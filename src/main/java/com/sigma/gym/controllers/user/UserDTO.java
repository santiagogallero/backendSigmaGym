package com.sigma.gym.controllers.user;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sigma.gym.entity.MembershipType;
import com.sigma.gym.entity.Progress;
import com.sigma.gym.entity.Role;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
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
@JsonIgnore
private String password;
    @NotNull
   private List<Role> roles;

    @JsonIgnore
    private LocalDate startDate; // Fecha de alta
    @JsonIgnore
    private LocalDate lastVisitDate; 
    @JsonIgnore
    private MembershipType membershipType; // Mensual, Trimestral, Premium, etc.
    @JsonIgnore
    private Boolean isActive; // ¿Está al día?
    @JsonIgnore
    private List<com.sigma.gym.entity.WorkoutPlan> workoutPlans;  // planes de entrenamiento asignados
    @JsonIgnore
    private List<Progress> progressHistory;  // peso levantado, tiempos, fotos, etc.

  
}