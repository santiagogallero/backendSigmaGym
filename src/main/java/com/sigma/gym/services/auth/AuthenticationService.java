package com.sigma.gym.services.auth;

import com.sigma.gym.controllers.auth.dtos.AuthenticationRequest;
import com.sigma.gym.controllers.auth.dtos.AuthenticationResponse;
import com.sigma.gym.controllers.auth.dtos.RegisterRequest;
import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity;
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

public AuthenticationResponse register(RegisterRequest request) {
    RoleEntity role = roleRepository.findById(request.getRoleId())
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + request.getRoleId()));

    UserEntity user = UserEntity.builder()
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .email(request.getEmail())
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .roles(Set.of(role)) // asigna el rol buscado
            .isActive(true)
            .build();

    userRepository.save(user);

    String jwtToken = jwtService.generateToken(user);

    return AuthenticationResponse.builder()
            .accessToken(jwtToken)
            .username(user.getUsername())
            .email(user.getEmail())
            .roles(Set.of(role.getName())) // para que el front reciba el nombre del rol
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

            UserEntity user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new Exception("User not found"));

            String token = jwtService.generateToken(user);

            return AuthenticationResponse.builder()
        .accessToken(token)
        .email(user.getEmail())
        .username(user.getUsername())
        .roles(user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet()))
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .build();

        } catch (AuthenticationException e) {
            throw new AuthException("Invalid email or password");
        }
    }
    public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
}