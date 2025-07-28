package com.sigma.gym.services.auth;

import com.sigma.gym.controllers.auth.dtos.AuthenticationRequest;
import com.sigma.gym.controllers.auth.dtos.AuthenticationResponse;
import com.sigma.gym.controllers.auth.dtos.RegisterRequest;
import com.sigma.gym.entity.Role;
import com.sigma.gym.entity.User;
import com.sigma.gym.repository.RoleRepository;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.security.JwtService;

import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
        user.setRoles(List.of(memberRole)); // ✅ Cambiado a lista

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
        .accessToken(token)
        .email(user.getEmail())
        .username(user.getUsername())
        .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .build();
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

            return AuthenticationResponse.builder()
        .accessToken(token)
        .email(user.getEmail())
        .username(user.getUsername())
        .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .build();

        } catch (AuthenticationException e) {
            throw new AuthException("Invalid email or password");
        }
    }
}
