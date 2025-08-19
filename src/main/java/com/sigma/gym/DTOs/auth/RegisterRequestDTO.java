package com.sigma.gym.DTOs.auth;

import com.sigma.gym.entity.RoleEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;

@Data
public class RegisterRequestDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;
    
    @NotNull(message = "Age is required")
    @Min(value = 13, message = "Age must be at least 13")
    @Max(value = 100, message = "Age cannot exceed 100")
    private Integer age;
    
    @NotBlank(message = "Health condition is required")
    @Size(max = 120, message = "Health condition cannot exceed 120 characters")
    private String healthCondition;
    
    @NotNull(message = "Role is required")
    private RoleEntity.RoleName role;
}
