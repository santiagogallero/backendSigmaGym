package com.sigma.gym.DTOs;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {
    private Long id;
    private String name;
    private int priority; // 1 = más poder (OWNER), 2 = TRAINER, 3 = MEMBER
}
