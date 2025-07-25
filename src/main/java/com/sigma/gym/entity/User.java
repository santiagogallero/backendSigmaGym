package com.sigma.gym.entity;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sigma.gym.controllers.user.UserDTO;
import com.sigma.gym.entities.Attendance;
import com.sigma.gym.entities.Progress;
import com.sigma.gym.entities.WorkoutPlan;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User implements UserDetails {
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

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;


    private LocalDate startDate;
    private LocalDate lastVisitDate;

    private String membershipType;
    private LocalDate membershipExpirationDate;
    private Boolean isActive;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<WorkoutPlan> workoutPlans;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Progress> progressHistory;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Attendance> attendanceRecords;

    public void updateData(User newUser){
        setFirstName(newUser.getFirstName());
        setLastName(newUser.getLastName());
        setEmail(newUser.getEmail());
    }

   
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.getName()));
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


    
