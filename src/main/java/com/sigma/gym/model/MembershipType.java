package com.sigma.gym.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipType {
    private Long id;
    private String name;
    private Integer allowedDaysPerWeek;
    private Integer durationInDays;
    private boolean isActive;
}
