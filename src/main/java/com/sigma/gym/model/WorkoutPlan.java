package com.sigma.gym.model;

import lombok.*;

import java.sql.Date;
import java.time.LocalDate;
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
    private Date startDate;
    private Date endDate;
    private String difficulty;
    private String notes;
    private LocalDate createdAt;
    private User trainer;
    private List<Routine> routines;
     private List<User> members;  
}
