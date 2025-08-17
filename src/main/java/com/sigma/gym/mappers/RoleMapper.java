package com.sigma.gym.mappers;
import com.sigma.gym.DTOs.RoleDTO;
import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.model.Role;
public class RoleMapper {

    public static Role toDomain(RoleEntity entity) {
        if (entity == null) return null;
        return Role.builder()
                .id(entity.getId())
                .name(entity.getName().name()) // Convert enum to string
                .priority(entity.getPriority())
                .build();
    }

    public static RoleEntity toEntity(Role domain) {
        if (domain == null) return null;
        return RoleEntity.builder()
                .id(domain.getId())
                .name(RoleEntity.RoleName.valueOf(domain.getName())) // Convert string to enum
                .priority(domain.getPriority())
                .build();
    }

    public static RoleDTO toDto(Role role) {
        if (role == null) return null;
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .priority(role.getPriority())
                .build();
    }

    public static Role toDomain(RoleDTO dto) {
        if (dto == null) return null;
        return Role.builder()
                .id(dto.getId())
                .name(dto.getName())
                .priority(dto.getPriority())
                .build();
    }

    public static RoleEntity toEntity(RoleDTO dto) {
        if (dto == null) return null;
        return RoleEntity.builder()
                .id(dto.getId())
                .name(RoleEntity.RoleName.valueOf(dto.getName())) // Convert string to enum
                .priority(dto.getPriority())
                .build();
    }

    public static RoleDTO toDto(RoleEntity entity) {
        if (entity == null) return null;
        return RoleDTO.builder()
                .id(entity.getId())
                .name(entity.getName().name()) // Convert enum to string
                .priority(entity.getPriority())
                .build();
    }
}
