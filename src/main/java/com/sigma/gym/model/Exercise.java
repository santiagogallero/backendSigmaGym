package com.sigma.gym.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Exercise {
    private Long id;
    private String name;
    private String description;
    private String category;
    private String equipment;
    private Integer duration;
    private Integer sets;
    private Integer reps;
    private String videoUrl;
    private User createdBy;
}
