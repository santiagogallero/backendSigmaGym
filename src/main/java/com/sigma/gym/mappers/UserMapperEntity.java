package com.sigma.gym.mappers;

import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.controllers.user.UserDTO;
import com.sigma.gym.mappers.*;

import java.util.stream.Collectors;

public class UserMapperEntity {

    public static UserDTO toDto(UserEntity user) {
        if (user == null) return null;

        return UserDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRoles() != null
                ? user.getRoles().stream().map(RoleMapper::toDto).collect(Collectors.toList())
                : null)
            .startDate(user.getStartDate())
            .lastVisitDate(user.getLastVisitDate())
            .membershipType(user.getMembershipType() != null
                ? MembershipTypeMapper.toDto(user.getMembershipType())
                : null)
            .isActive(user.getIsActive())
            .workoutPlans(user.getWorkoutPlans() != null
                ? user.getWorkoutPlans().stream().map(WorkoutPlanMapper::toDto).collect(Collectors.toList())
                : null)
            .progressHistory(user.getProgressHistory() != null
                ? user.getProgressHistory().stream().map(ProgressMapper::toDto).collect(Collectors.toList())
                : null)
            .build();
    }

    public static UserEntity toEntity(UserDTO dto) {
        if (dto == null) return null;

        return UserEntity.builder()
            .id(dto.getId())
            .username(dto.getUsername())
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .email(dto.getEmail())
            .password(dto.getPassword())
            .roles(dto.getRoles() != null
                ? dto.getRoles().stream().map(RoleMapper::toEntity).collect(Collectors.toList())
                : null)
            .startDate(dto.getStartDate())
            .lastVisitDate(dto.getLastVisitDate())
            .membershipType(dto.getMembershipType() != null
                ? MembershipTypeMapper.toEntity(dto.getMembershipType())
                : null)
            .isActive(dto.getIsActive())
            .workoutPlans(dto.getWorkoutPlans() != null
                ? dto.getWorkoutPlans().stream().map(WorkoutPlanMapper::toEntity).collect(Collectors.toList())
                : null)
            .progressHistory(dto.getProgressHistory() != null
                ? dto.getProgressHistory().stream().map(ProgressMapper::toEntity).collect(Collectors.toList())
                : null)
            .build();
    }
}
