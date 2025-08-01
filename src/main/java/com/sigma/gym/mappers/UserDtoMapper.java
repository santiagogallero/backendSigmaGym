package com.sigma.gym.mappers;

import com.sigma.gym.controllers.user.UserDTO;
import com.sigma.gym.model.User;

public class UserDtoMapper {

    public static UserDTO toDto(User user) {
        if (user == null) return null;

        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(
                    user.getRoles() != null
                        ? user.getRoles().stream()
                           .map(RoleMapper::toDto)
                            .toList()
                        : null
                )
                .startDate(user.getStartDate())
                .lastVisitDate(user.getLastVisitDate())
                .membershipType(user.getMembershipType() != null 
                    ? MembershipTypeMapper.toDto(user.getMembershipType()) // ✅ FIXED: Use correct mapper method
                    : null)
                .isActive(user.getIsActive())
                .workoutPlans(user.getAssignedPlans() != null
                    ? user.getAssignedPlans().stream()
                        .map(WorkoutPlanMapper::toDto)
                        .toList()
                    : null)
                .build();
    }

    public static User toDomain(UserDTO dto) {
        if (dto == null) return null;

        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .email(dto.getEmail())
                .roles(
                    dto.getRoles() != null 
                        ? dto.getRoles().stream()
                            .map(RoleMapper::toDomain)
                            .toList()
                        : null
                )
                .startDate(dto.getStartDate())
                .lastVisitDate(dto.getLastVisitDate())
                .membershipType(dto.getMembershipType() != null
                    ? MembershipTypeMapper.toDomain(dto.getMembershipType()) // ✅ FIXED: Use correct mapper method
                    : null)
                .isActive(dto.getIsActive())
                .assignedPlans(dto.getWorkoutPlans() != null
                    ? dto.getWorkoutPlans().stream()
                        .map(WorkoutPlanMapper::toDomain)
                        .toList()
                    : null)
                .build();
    }
}
