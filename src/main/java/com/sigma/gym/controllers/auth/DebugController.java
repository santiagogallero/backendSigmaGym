package com.sigma.gym.controllers.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sigma.gym.config.JwtProperties;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final JwtProperties jwtProperties;

    public DebugController(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @GetMapping("/jwt")
    public String showJwtSecret() {
        return jwtProperties.getSecretKey(); // Si esto devuelve algo, ¡está funcionando!
    }
}
