package com.sigma.gym.DTOs.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private UserInfoDTO user;
}
