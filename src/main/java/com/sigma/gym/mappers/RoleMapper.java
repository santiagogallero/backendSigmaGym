package com.sigma.gym.mappers;
import com.sigma.gym.DTOs.RoleDTO;
import com.sigma.gym.entity.RoleEntity;
import com.sigma.gym.model.Role;

public class RoleMapper {
    
    public static Role toDomain(RoleEntity entity) {
        if (entity == null) return null;
        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }

    public static RoleEntity toEntity(Role role) {
        if (role == null) return null;
        return RoleEntity.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public static RoleDTO toDto(Role role) {
        if (role == null) return null;
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }

    public static Role toDomain(RoleDTO dto) {
        if (dto == null) return null;
        return Role.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
    }
}
