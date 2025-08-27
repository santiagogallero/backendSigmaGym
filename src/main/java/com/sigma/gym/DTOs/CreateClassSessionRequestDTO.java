package com.sigma.gym.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateClassSessionRequestDTO {
    @NotBlank private String className;
    @NotBlank private String classType;
    private String description;
    @NotNull private Long trainerId;
    private Long programId;
    private Long coachId;
    private Long locationId;
    @NotNull @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime startsAt;
    @NotNull @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime endsAt;
    @Min(1) private Integer capacity = 10;
}
