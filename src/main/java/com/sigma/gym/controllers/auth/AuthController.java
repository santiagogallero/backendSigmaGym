// src/main/java/com/sigma/gym/controller/AuthController.java
package com.sigma.gym.controllers.auth;

import com.sigma.gym.DTOs.auth.AuthResponseDTO;
import com.sigma.gym.DTOs.auth.LoginRequestDTO;
import com.sigma.gym.DTOs.auth.RegisterRequestDTO;
import com.sigma.gym.DTOs.auth.UserInfoDTO;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.response.ResponseData;
import com.sigma.gym.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<ResponseData<AuthResponseDTO>> login(@Valid @RequestBody LoginRequestDTO request) {
        try {
            AuthResponseDTO response = authService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(ResponseData.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseData.error("Invalid email or password"));
        }
    }

    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseData<AuthResponseDTO>> register(@Valid @RequestBody RegisterRequestDTO request) {
        try {
            AuthResponseDTO response = authService.register(
                    request.getEmail(),
                    request.getPassword(),
                    request.getFirstName(),
                    request.getLastName(),
                    request.getRole()
            );
            return ResponseEntity.ok(ResponseData.success(response));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("Email already registered")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(ResponseData.error("Email already registered"));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseData.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.error("Registration failed"));
        }
    }

    /**
     * Get current authenticated user info
     */
    @GetMapping("/me")
    public ResponseEntity<ResponseData<UserInfoDTO>> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication != null && authentication.getPrincipal() instanceof UserEntity) {
                UserEntity user = (UserEntity) authentication.getPrincipal();
                UserInfoDTO userInfo = authService.getCurrentUser(user.getEmail());
                return ResponseEntity.ok(ResponseData.success(userInfo));
            }
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseData.error("User not authenticated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseData.error("Failed to get user info"));
        }
    }

    /**
     * Logout endpoint (client-side token removal)
     */
    @PostMapping("/logout")
    public ResponseEntity<ResponseData<String>> logout() {
        // Since JWT is stateless, logout is handled client-side by removing the token
        return ResponseEntity.ok(ResponseData.success("Logout successful"));
    }

    /**
     * Legacy authenticate endpoint (alias for login for backward compatibility)
     */
    @PostMapping("/authenticate")
    public ResponseEntity<ResponseData<AuthResponseDTO>> authenticate(@RequestBody LoginRequestDTO request) {
        return login(request);
    }
}