package com.sigma.gym.model;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String password; // ⚠️ Solo si lo vas a usar en el modelo
    private Boolean isActive;
    private LocalDate startDate;
    private LocalDate lastVisitDate;

    private MembershipType membershipType;
    private List<Role> roles;

    private List<WorkoutPlan> workoutPlans;       // Planes creados (si es entrenador)
    private List<WorkoutPlan> assignedPlans;      // Planes asignados (si es miembro)

    private List<Attendance> attendanceRecords;
    private List<Progress> progressHistory;
    private List<WorkoutLog> workoutLogs;
    private List<RoutineProgress> routineProgressList;
}
