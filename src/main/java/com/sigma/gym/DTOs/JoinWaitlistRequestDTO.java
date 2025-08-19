package com.sigma.gym.DTOs;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JoinWaitlistRequestDTO {
    
    @Size(max = 500, message = "Note cannot exceed 500 characters")
    private String note;
}
