package com.sigma.gym.mappers;

package com.sigma.gym.mappers;

import com.sigma.gym.entity.User;
import com.sigma.gym.dtos.UserDto;

public class UserMapper {

    public static UserDto toDto(User user) {
        if (user == null) return null;

        return new UserDto(
            user.getId(),
            user.getUsername(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.getPassword(),
            user.getRole(),
            null,
            user.getStartDate(),
            user.getLastVisitDate(),
            user.getMembershipType(),
            user.getMembershipExpirationDate(),
            user.getIsActive(),
            user.getWorkoutPlans(),
            user.getProgressHistory()
        );
    }

    public static User toEntity(UserDto dto) {
        if (dto == null) return null;

        return User.builder()
            .id(dto.getId())
            .username(dto.getUsername())
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .email(dto.getEmail())
            .password(dto.getPassword())
            .role(dto.getRole())
            .orders(null)
            .startDate(dto.getStartDate())
            .lastVisitDate(dto.getLastVisitDate())
            .membershipType(dto.getMembershipType())
            .membershipExpirationDate(dto.getMembershipExpirationDate())
            .isActive(dto.getIsActive())
            .workoutPlans(dto.getWorkoutPlans())
            .progressHistory(dto.getProgressHistory())
            .build();
    }
}
