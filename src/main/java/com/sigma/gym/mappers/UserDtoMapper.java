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
           .map(RoleMapper::toDto)// <--- convierte del modelo al DTO
            .toList()
        : null
)

                .startDate(user.getStartDate())
                .lastVisitDate(user.getLastVisitDate())
                .membershipType(user.getMembershipType())
                .isActive(user.getIsActive())
                .workoutPlans(user.getAssignedPlans()) // 👈 nombre correcto según tu modelo
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
                // ⚠️ dto.getPassword() no se guarda en el modelo User
                .roles(
                    dto.getRoles() != null 
                        ? dto.getRoles().stream()
                            .map(RoleMapper::toDomain)
                            .toList()
                        : null
                )

                .startDate(dto.getStartDate())
                .lastVisitDate(dto.getLastVisitDate())
                .membershipType(dto.getMembershipType())
                .isActive(dto.getIsActive())
                .assignedPlans(dto.getWorkoutPlans()) // 👈 corresponde con el atributo del modelo
                .build();
    }
}
