package com.sigma.gym.controllers.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 13, message = "La edad mínima es 13 años")
    @Max(value = 100, message = "La edad máxima es 100 años")
    private Integer age;

    @NotBlank(message = "La condición de salud es obligatoria")
    @Size(max = 120, message = "La condición de salud no puede exceder 120 caracteres")
    private String healthCondition;


}
