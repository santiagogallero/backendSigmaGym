package com.sigma.gym.controllers.auth;

import com.sigma.gym.DTOs.auth.AuthResponseDTO;
import com.sigma.gym.DTOs.auth.RegisterRequestDTO;
import com.sigma.gym.DTOs.auth.UserInfoDTO;
import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.mappers.UserMapper;
import com.sigma.gym.repository.RoleRepository;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.response.ResponseData;
import com.sigma.gym.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
public class RegistrationController {
    
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<ResponseData<AuthResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            // 1) Validate uniqueness
            if (userRepository.existsByEmail(request.getEmail())) {
                return ResponseEntity.badRequest()
                        .body(ResponseData.error("EMAIL_TAKEN", "Email already in use"));
            }

            // 2) Force role = MEMBER (ignore any incoming role)
            RoleEntity memberRole = roleRepository.findByName(RoleEntity.RoleName.MEMBER)
                    .orElseThrow(() -> new IllegalStateException("MEMBER role missing"));

            // 3) Create user
            UserEntity user = new UserEntity();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setAge(request.getAge());
            user.setHealthCondition(request.getHealthCondition());
            user.setRoles(Set.of(memberRole));
            user.setIsActive(true);
            user.setStartDate(LocalDate.now());

            user = userRepository.save(user);

            // 4) Issue JWT as usual
            String token = jwtService.generateToken(user);
            UserInfoDTO userInfo = UserMapper.toUserInfoDTO(user);
            
            AuthResponseDTO response = AuthResponseDTO.builder()
                    .token(token)
                    .type("Bearer")
                    .user(userInfo)
                    .build();

            return ResponseEntity.ok(ResponseData.ok(response));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ResponseData.error("REGISTRATION_ERROR", "Registration failed: " + e.getMessage()));
        }
    }
}
