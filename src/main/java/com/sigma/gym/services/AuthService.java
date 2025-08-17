package com.sigma.gym.services;

import com.sigma.gym.DTOs.auth.AuthResponseDTO;
import com.sigma.gym.DTOs.auth.UserInfoDTO;
import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.mappers.UserMapper;
import com.sigma.gym.repository.RoleRepository;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDTO login(String email, String password) {
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        // Get user details
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update last login timestamp
        user.updateLastLoginAt();
        userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(user);

        // Convert to DTO and return
        return AuthResponseDTO.builder()
                .token(token)
                .user(UserMapper.toUserInfoDTO(user))
                .build();
    }

    @Transactional
    public AuthResponseDTO register(String email, String password, String firstName, String lastName, RoleEntity.RoleName roleName) {
        // Check if user already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        // Get role
        RoleEntity role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        // Create new user
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoles(Set.of(role));
        user.setIsActive(true);

        UserEntity savedUser = userRepository.save(user);

        // Generate JWT token
        String token = jwtService.generateToken(savedUser);

        // Convert to DTO and return
        return AuthResponseDTO.builder()
                .token(token)
                .user(UserMapper.toUserInfoDTO(savedUser))
                .build();
    }

    @Transactional(readOnly = true)
    public UserInfoDTO getCurrentUser(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toUserInfoDTO(user);
    }

    @Transactional
    public void updateLastLoginAt(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.updateLastLoginAt();
        userRepository.save(user);
    }
}
