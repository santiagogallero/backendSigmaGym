package com.sigma.gym.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private Long id;
    private String name;
    private int priority; // 1 = más poder (OWNER), 2 = TRAINER, 3 = MEMBER
}
