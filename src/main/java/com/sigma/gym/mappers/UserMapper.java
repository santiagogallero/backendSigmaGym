package com.sigma.gym.mappers;

import com.sigma.gym.controllers.user.UserDTO;
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

        return UserEntity.builder()
            .id(dto.getId())
            .username(dto.getUsername())
            .firstName(dto.getFirstName())
            .lastName(dto.getLastName())
            .email(dto.getEmail())
            .roles(dto.getRoles() != null
            ? dto.getRoles().stream()
                .map(RoleMapper::toEntity)
                .collect(Collectors.toSet()) // ✅ ahora coincide con Set<RoleEntity>
            : null)

            .startDate(dto.getStartDate())
            .lastVisitDate(dto.getLastVisitDate())
            .membershipType(dto.getMembershipType() != null
                ? MembershipTypeMapper.toEntityFromDto(dto.getMembershipType())
                : null)
            .isActive(dto.getIsActive())
            .workoutPlans(dto.getWorkoutPlans() != null
                ? dto.getWorkoutPlans().stream()
                    .map(WorkoutPlanMapper::toEntity)
                    .collect(Collectors.toList())
                : null)
            .progressHistory(dto.getProgressHistory() != null
                ? dto.getProgressHistory().stream()
                    .map(ProgressMapper::toEntity)
                    .collect(Collectors.toList())
                : null)
            .build();
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

        return UserEntity.builder()
            .id(domain.getId())
            .username(domain.getUsername())
            .firstName(domain.getFirstName())
            .lastName(domain.getLastName())
            .email(domain.getEmail())
            .roles(domain.getRoles() != null
    ? domain.getRoles().stream()
        .map(RoleMapper::toEntity)
        .collect(Collectors.toSet()) // ✅ ahora devuelve Set<RoleEntity>
    : null)

            .startDate(domain.getStartDate())
            .lastVisitDate(domain.getLastVisitDate())
            .membershipType(domain.getMembershipType() != null
                ? MembershipTypeMapper.toEntity(domain.getMembershipType())
                : null)
            .isActive(domain.getIsActive())
            .workoutPlans(domain.getWorkoutPlans() != null
                ? domain.getWorkoutPlans().stream()
                    .map(WorkoutPlanMapper::toEntity)
                    .collect(Collectors.toList())
                : null)
            .progressHistory(domain.getProgressHistory() != null
                ? domain.getProgressHistory().stream()
                    .map(ProgressMapper::toEntity)
                    .collect(Collectors.toList())
                : null)
            .build();
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
}
