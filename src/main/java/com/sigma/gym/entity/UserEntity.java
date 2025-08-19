package com.sigma.gym.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.*;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class UserEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Column(nullable = false)
    private String password;
    
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    @Column(nullable = false)
    private String firstName;
    
        @Column(name = "last_name")
    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 50, message = "El apellido no puede exceder 50 caracteres")
    private String lastName;

    @Column(name = "age")
    @NotNull(message = "La edad es obligatoria")
    @Min(value = 13, message = "La edad mínima es 13 años")
    @Max(value = 100, message = "La edad máxima es 100 años")
    private Integer age;

    @Column(name = "health_condition")
    @NotBlank(message = "La condición de salud es obligatoria")
    @Size(max = 120, message = "La condición de salud no puede exceder 120 caracteres")
    private String healthCondition;

@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<RoleEntity> roles = new HashSet<>();

@Column(nullable = false)
private Boolean isActive = true;

@CreationTimestamp
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt;

@Column(name = "last_login_at")
private LocalDateTime lastLoginAt;



    private LocalDate startDate;
    private LocalDate lastVisitDate;
    
    @ManyToOne
    @JoinColumn(name = "membership_type_id")
    private MembershipTypeEntity membershipType;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<RoutineProgressEntity> routineProgressList;

    @OneToMany(mappedBy = "trainer", cascade = CascadeType.ALL)
    private List<WorkoutPlanEntity> workoutPlans;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WorkOutLogEntity> workoutLogs;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ProgressEntity> progressHistory;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<AttendanceEntity> attendanceRecords;

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<ExerciseEntity> createdExercises;

    @ManyToMany
    @JoinTable(
        name = "user_workout_plan",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "plan_id")
    )
    private List<WorkoutPlanEntity> assignedPlans;


    public void updateData(UserEntity newUser){
        setFirstName(newUser.getFirstName());
        setLastName(newUser.getLastName());
        setEmail(newUser.getEmail());
    }

    // Method to update last login timestamp
    public void updateLastLoginAt() {
        this.lastLoginAt = LocalDateTime.now();
    }

    @Override
    public String getUsername() {
        return email; // Use email as username for authentication
    }

@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles != null
        ? roles.stream()
               .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
               .toList()
        : new ArrayList<>();
}

// dentro de la clase User
public boolean hasRole(RoleEntity.RoleName roleName) {
    return roles != null && roles.stream()
            .anyMatch(r -> r.getName().equals(roleName));
}

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive != null ? isActive : true; // o false por defecto si querés revisar manual
    }


}



