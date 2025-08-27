package com.sigma.gym.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBookingRequestDTO {
    @NotNull
    private Long classSessionId;
    private String notes;
}
