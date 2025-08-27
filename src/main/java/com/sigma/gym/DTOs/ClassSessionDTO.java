package com.sigma.gym.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sigma.gym.entity.ClassSessionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassSessionDTO {
    private Long id;
    private String className;
    private String classType;
    private String description;
    private Long trainerId;
    private Integer capacity;
    private Integer bookedCount;
    private Boolean isActive;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime startsAt;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") private LocalDateTime endsAt;

    public static ClassSessionDTO fromEntity(ClassSessionEntity e) {
        return ClassSessionDTO.builder()
            .id(e.getId())
            .className(e.getClassName())
            .classType(e.getClassType())
            .description(e.getDescription())
            .trainerId(e.getTrainerId())
            .capacity(e.getCapacity())
            .bookedCount(e.getBookedCount())
            .isActive(e.getIsActive())
            .startsAt(e.getStartsAt())
            .endsAt(e.getEndsAt())
            .build();
    }
}
