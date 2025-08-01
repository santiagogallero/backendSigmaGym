package com.sigma.gym.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;
import lombok.*;
@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class UserEntity implements UserDetails {
       @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column(unique = true)
    private String email;
    @Column(nullable = false)
    private String password;

@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
    name = "user_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
    private List<RoleEntity> roles;


    private LocalDate startDate;
    private LocalDate lastVisitDate;
    @ManyToOne
    @JoinColumn(name = "membership_type_id")
    private MembershipTypeEntity membershipType;
    
    private Boolean isActive;
    
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

   
@Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return roles != null
        ? roles.stream()
               .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
               .toList()
        : new ArrayList<>();
}

    // dentro de la clase User
public boolean hasRole(String roleName) {
    return roles != null && roles.stream()
            .anyMatch(r -> r.getName().equalsIgnoreCase(roleName));
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


    
