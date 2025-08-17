package com.sigma.gym.mappers;

import com.sigma.gym.controllers.user.UserDTO;
import com.sigma.gym.DTOs.auth.UserInfoDTO;
import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.model.User;
import java.util.stream.Collectors;

/**
 * Mapper for User domain conversions between Entity, Domain, and DTO layers
 */
public class UserMapper {

    /**
     * Converts UserEntity to UserDTO for API responses
     */
    public static UserDTO toDto(UserEntity entity) {
        if (entity == null) return null;

        return UserDTO.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .email(entity.getEmail())
            .roles(entity.getRoles() != null
                ? entity.getRoles().stream()
                    .map(RoleMapper::toDto)
                    .collect(Collectors.toList())
                : null)
            .startDate(entity.getStartDate())
            .lastVisitDate(entity.getLastVisitDate())
            .membershipType(entity.getMembershipType() != null
                ? MembershipTypeMapper.toDto(entity.getMembershipType())
                : null)
            .isActive(entity.getIsActive())
            .workoutPlans(entity.getWorkoutPlans() != null
                ? entity.getWorkoutPlans().stream()
                    .map(WorkoutPlanMapper::toDto)
                    .collect(Collectors.toList())
                : null)
            .progressHistory(entity.getProgressHistory() != null
                ? entity.getProgressHistory().stream()
                    .map(ProgressMapper::toDto)
                    .collect(Collectors.toList())
                : null)
            .build();
    }

    /**
     * Converts UserDTO to UserEntity for persistence
     */
    public static UserEntity toEntity(UserDTO dto) {
    if (dto == null) return null;

    UserEntity entity = new UserEntity();
    entity.setId(dto.getId());
    // Remove setUsername as it no longer exists
    entity.setFirstName(dto.getFirstName());
    entity.setLastName(dto.getLastName());
    entity.setEmail(dto.getEmail());
    entity.setRoles(dto.getRoles() != null
        ? dto.getRoles().stream()
            .map(RoleMapper::toEntity)
            .collect(Collectors.toSet())
        : null);
    entity.setStartDate(dto.getStartDate());
    entity.setLastVisitDate(dto.getLastVisitDate());
    entity.setMembershipType(dto.getMembershipType() != null
        ? MembershipTypeMapper.toEntityFromDto(dto.getMembershipType())
        : null);
    entity.setIsActive(dto.getIsActive());
    // Solo asignar listas hijas si es un update (por ejemplo, si el DTO tiene id)
    if (dto.getId() != null) {
        if (dto.getWorkoutPlans() != null) {
            entity.setWorkoutPlans(dto.getWorkoutPlans().stream()
                .map(WorkoutPlanMapper::toEntity)
                .collect(Collectors.toList()));
        }
        if (dto.getProgressHistory() != null) {
            entity.setProgressHistory(dto.getProgressHistory().stream()
                .map(ProgressMapper::toEntity)
                .collect(Collectors.toList()));
        }
        // Agregar aquí el resto de listas hijas solo si es necesario para updates
    }
    // Para registro, no se asignan listas hijas
    return entity;
}

    /**
     * Converts UserEntity to User domain model
     */
    public static User toDomain(UserEntity entity) {
        if (entity == null) return null;

        return User.builder()
            .id(entity.getId())
            .username(entity.getUsername())
            .firstName(entity.getFirstName())
            .lastName(entity.getLastName())
            .email(entity.getEmail())
            .roles(entity.getRoles() != null
                ? entity.getRoles().stream()
                    .map(RoleMapper::toDomain)
                    .collect(Collectors.toList())
                : null)
            .startDate(entity.getStartDate())
            .lastVisitDate(entity.getLastVisitDate())
            .membershipType(entity.getMembershipType() != null
                ? MembershipTypeMapper.toDomain(entity.getMembershipType())
                : null)
            .isActive(entity.getIsActive())
            // Opcional: Mapear solo IDs para evitar referencias circulares
            .workoutPlans(entity.getWorkoutPlans() != null
                ? entity.getWorkoutPlans().stream()
                    .map(WorkoutPlanMapper::toDomain)
                    .collect(Collectors.toList())
                : null)
            .progressHistory(entity.getProgressHistory() != null
                ? entity.getProgressHistory().stream()
                    .map(ProgressMapper::toDomain)
                    .collect(Collectors.toList())
                : null)
            .build();
    }

    /**
     * Converts User domain model to UserEntity
     */
public static UserEntity toEntity(User domain) {
    if (domain == null) return null;

    UserEntity entity = new UserEntity();
    entity.setId(domain.getId());
    // Remove setUsername as it no longer exists in entity
    entity.setFirstName(domain.getFirstName());
    entity.setLastName(domain.getLastName());
    entity.setEmail(domain.getEmail());

    if (domain.getRoles() != null) {
        entity.setRoles(domain.getRoles().stream()
            .map(RoleMapper::toEntity)
            .collect(Collectors.toSet()));
    }

    entity.setStartDate(domain.getStartDate());
    entity.setLastVisitDate(domain.getLastVisitDate());

    if (domain.getMembershipType() != null) {
        entity.setMembershipType(MembershipTypeMapper.toEntity(domain.getMembershipType()));
    }

    entity.setIsActive(domain.getIsActive());

    // Solo mapear listas hijas si es un update (por ejemplo, si el dominio tiene id)
    if (domain.getId() != null) {
        if (domain.getWorkoutPlans() != null) {
            entity.setWorkoutPlans(domain.getWorkoutPlans().stream()
                .map(WorkoutPlanMapper::toEntity)
                .collect(Collectors.toList()));
        }
        if (domain.getProgressHistory() != null) {
            entity.setProgressHistory(domain.getProgressHistory().stream()
                .map(ProgressMapper::toEntity)
                .collect(Collectors.toList()));
        }
        // Agregar aquí el resto de listas hijas solo si es necesario para updates
    }
    // Para registro, no se asignan listas hijas
    return entity;
}

    /**
     * Converts User domain model to UserDTO
     */
    public static UserDTO toDto(User domain) {
        if (domain == null) return null;

        return UserDTO.builder()
            .id(domain.getId())
            .username(domain.getUsername())
            .firstName(domain.getFirstName())
            .lastName(domain.getLastName())
            .email(domain.getEmail())
            .roles(domain.getRoles() != null
                ? domain.getRoles().stream()
                    .map(RoleMapper::toDto)
                    .collect(Collectors.toList())
                : null)
            .startDate(domain.getStartDate())
            .lastVisitDate(domain.getLastVisitDate())
            .membershipType(domain.getMembershipType() != null
                ? MembershipTypeMapper.toDto(domain.getMembershipType())
                : null)
            .isActive(domain.getIsActive())
            .workoutPlans(domain.getWorkoutPlans() != null
                ? domain.getWorkoutPlans().stream()
                    .map(WorkoutPlanMapper::toDto)
                    .collect(Collectors.toList())
                : null)
            .progressHistory(domain.getProgressHistory() != null
                ? domain.getProgressHistory().stream()
                    .map(ProgressMapper::toDto)
                    .collect(Collectors.toList())
                : null)
            .build();
    }

    /**
     * Converts UserDTO to User domain model
     */
    public static User toDomain(UserDTO dto) {
        if (dto == null) return null;

        return User.builder()
            .id(dto.getId())
            .username(dto.getUsername())
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .email(dto.getEmail())
            .roles(dto.getRoles() != null
                ? dto.getRoles().stream()
                    .map(RoleMapper::toDomain)
                    .collect(Collectors.toList())
                : null)
            .startDate(dto.getStartDate())
            .lastVisitDate(dto.getLastVisitDate())
            .membershipType(dto.getMembershipType() != null
                ? MembershipTypeMapper.toDomain(dto.getMembershipType())
                : null)
            .isActive(dto.getIsActive())
            .workoutPlans(dto.getWorkoutPlans() != null
                ? dto.getWorkoutPlans().stream()
                    .map(WorkoutPlanMapper::toDomain)
                    .collect(Collectors.toList())
                : null)
            // Nota: progressHistory no está en UserDTO, considerar si agregarlo
            .build();
    }

    /**
     * Simplified conversion for basic User data without nested objects
     * Useful for avoiding circular references
     */
    public static UserDTO toBasicDto(User domain) {
        if (domain == null) return null;

        return UserDTO.builder()
            .id(domain.getId())
            .username(domain.getUsername())
            .firstName(domain.getFirstName())
            .lastName(domain.getLastName())
            .email(domain.getEmail())
            .startDate(domain.getStartDate())
            .lastVisitDate(domain.getLastVisitDate())
            .isActive(domain.getIsActive())
            .build();
    }

    /**
     * Converts UserEntity to UserInfoDTO for authentication responses
     */
    public static UserInfoDTO toUserInfoDTO(UserEntity user) {
        if (user == null) {
            return null;
        }
        
        return UserInfoDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles() != null 
                        ? user.getRoles().stream()
                                .map(RoleEntity::getName)
                                .collect(Collectors.toSet())
                        : null)
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }
}
