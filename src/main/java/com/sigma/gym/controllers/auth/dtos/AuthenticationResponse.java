package com.sigma.gym.controllers.auth.dtos;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    @NotNull
    @JsonProperty("access_token")
    private String accessToken;

    @NotNull
    private String username;

    @NotNull
    private String email;

    private Set<String> roles;

    private String firstName;

    private String lastName;
    
}
