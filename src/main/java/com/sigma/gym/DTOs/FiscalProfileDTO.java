package com.sigma.gym.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FiscalProfileDTO {
    
    private Long id;
    private Long userId;
    
    @NotBlank(message = "Legal name is required")
    @Size(max = 200, message = "Legal name must not exceed 200 characters")
    private String legalName;
    
    @Size(max = 20, message = "Tax ID must not exceed 20 characters")
    private String taxId; // CUIT format: 20-12345678-3
    
    @NotBlank(message = "Document type is required")
    @Size(max = 10, message = "Document type must not exceed 10 characters")
    private String documentType; // DNI, CUIT, CUIL
    
    @NotBlank(message = "Document number is required")
    @Size(max = 20, message = "Document number must not exceed 20 characters")
    private String documentNumber;
    
    @Size(max = 300, message = "Address line must not exceed 300 characters")
    private String addressLine;
    
    @Size(max = 100, message = "City must not exceed 100 characters")
    private String city;
    
    @Size(max = 100, message = "State must not exceed 100 characters")
    private String state;
    
    @Size(max = 20, message = "Postal code must not exceed 20 characters")
    private String postalCode;
    
    @Size(max = 3, message = "Country must not exceed 3 characters")
    @Builder.Default
    private String country = "AR";
}
