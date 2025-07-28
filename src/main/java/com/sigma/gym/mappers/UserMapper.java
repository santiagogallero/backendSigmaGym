package com.sigma.gym.mappers;

import com.sigma.gym.entity.User;

import lombok.Builder;
import lombok.Data;
import com.sigma.gym.controllers.user.UserDTO;
@Data
@Builder
public class UserMapper {
public static UserDTO toDto(User user) {
    if (user == null) return null;

    return UserDTO.builder()
        .id(user.getId())
        .username(user.getUsername())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .password(user.getPassword())
        .roles(user.getRoles())
        .startDate(user.getStartDate())
        .lastVisitDate(user.getLastVisitDate())
        .membershipType(user.getMembershipType())
        .isActive(user.getIsActive())
        .workoutPlans(user.getWorkoutPlans())
        .progressHistory(user.getProgressHistory())
        .build();
}

public static User toEntity(UserDTO dto) {
    if (dto == null) return null;

    return User.builder()
        .id(dto.getId())
        .username(dto.getUsername())
        .firstName(dto.getFirstName())
        .lastName(dto.getLastName())
        .email(dto.getEmail())
        .password(dto.getPassword())
        .roles(dto.getRoles())
        .startDate(dto.getStartDate())
        .lastVisitDate(dto.getLastVisitDate())
        .membershipType(dto.getMembershipType())
        .isActive(dto.getIsActive())
        .workoutPlans(dto.getWorkoutPlans())
        .progressHistory(dto.getProgressHistory())
        .build();
}
}
