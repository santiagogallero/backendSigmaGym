package com.sigma.gym.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExerciseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String category;
    private String equipment;
    private Integer duration;
    private Integer sets;
    private Integer reps;
    private String videoUrl;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserEntity createdBy;
}
