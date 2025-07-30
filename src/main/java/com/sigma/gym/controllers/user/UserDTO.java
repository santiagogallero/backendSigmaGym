package com.sigma.gym.controllers.user;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sigma.gym.DTOs.RoleDTO;
import com.sigma.gym.model.MembershipType;
import com.sigma.gym.model.Progress;
import com.sigma.gym.model.Role;
import com.sigma.gym.model.WorkoutPlan;

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
   private List<RoleDTO> roles;

    @JsonIgnore
    private LocalDate startDate;

    @JsonIgnore
    private LocalDate lastVisitDate;

    @JsonIgnore
    private MembershipType membershipType;

    @JsonIgnore
    private Boolean isActive;

    @JsonIgnore
    private List<WorkoutPlan> workoutPlans;

    @JsonIgnore
    private List<Progress> progressHistory;
}
