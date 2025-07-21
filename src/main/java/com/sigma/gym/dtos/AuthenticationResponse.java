// src/main/java/com/sigma/gym/dto/AuthenticationResponse.java
package com.sigma.gym.dtos;

import java.util.Set;

public class AuthenticationResponse {
    private String accessToken;
    private String email;
    private String name;
    private Set<String> roles;

    public AuthenticationResponse() {}

    public AuthenticationResponse(String accessToken, String email, String name, Set<String> roles) {
        this.accessToken = accessToken;
        this.email = email;
        this.name = name;
        this.roles = roles;
    }

    // Getters & setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
