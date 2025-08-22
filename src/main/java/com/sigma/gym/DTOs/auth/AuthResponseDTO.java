package com.sigma.gym.DTOs.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    
    // Alias para compatibilidad con frontends que esperan access_token
    @JsonProperty("access_token")
    public String getAccessToken() {
        return token;
    }
    
    @Builder.Default
    private String type = "Bearer";
    private UserInfoDTO user;
}
