// src/main/java/com/sigma/gym/service/AuthenticationService.java
package com.sigma.gym.services.auth;

import com.sigma.gym.controllers.auth.dtos.AuthenticationRequest;
import com.sigma.gym.controllers.auth.dtos.AuthenticationResponse;
import com.sigma.gym.controllers.auth.dtos.RegisterRequest;
import com.sigma.gym.entity.Role;
import com.sigma.gym.entity.User;
import com.sigma.gym.repository.RoleRepository;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.security.JwtService;

import jakarta.annotation.PostConstruct;
import jakarta.security.auth.message.AuthException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public AuthenticationResponse register(RegisterRequest request) throws Exception {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new Exception("Email already in use");
        }

        Role memberRole = roleRepository.findByName("MEMBER")
                .orElseThrow(() -> new RuntimeException("Role MEMBER not found"));

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Collections.singleton(memberRole));

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return new AuthenticationResponse(
                token,
                user.getEmail(),
                user.getUsername(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new Exception("User not found"));

            String token = jwtService.generateToken(user);

            return new AuthenticationResponse(
                    token,
                    user.getEmail(),
                    user.getUsername(),
                    user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
            );
        } catch (AuthenticationException e) {
            throw new AuthException("Invalid email or password");
        }
    }
    
}
