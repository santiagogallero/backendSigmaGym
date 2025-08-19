package com.sigma.gym.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutPreferenceDTO {
    
    private String preferenceId;
    private String initPoint;
    private String sandboxInitPoint;
    private String externalReference;
}
