// src/main/java/com/sigma/gym/controller/AuthController.java
package com.sigma.gym.controllers;

import com.sigma.gym.dtos.AuthenticationRequest;
import com.sigma.gym.dtos.AuthenticationResponse;
import com.sigma.gym.dtos.RegisterRequest;
import com.sigma.gym.services.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authService;

    public AuthController(AuthenticationService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) throws Exception {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody AuthenticationRequest request) throws Exception {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
