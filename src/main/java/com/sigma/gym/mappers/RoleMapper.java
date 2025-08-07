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
                .priority(entity.getPriority()) // ✅ agregado
                .build();
    }

    public static RoleEntity toEntity(Role domain) {
        if (domain == null) return null;
        return RoleEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .priority(domain.getPriority()) // ✅ agregado
                .build();
    }

    public static RoleDTO toDto(Role role) {
        if (role == null) return null;
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .priority(role.getPriority()) // ✅ agregado
                .build();
    }

    public static Role toDomain(RoleDTO dto) {
        if (dto == null) return null;
        return Role.builder()
                .id(dto.getId())
                .name(dto.getName())
                .priority(dto.getPriority()) // ✅ agregado
                .build();
    }

    public static RoleEntity toEntity(RoleDTO dto) {
        if (dto == null) return null;
        return RoleEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .priority(dto.getPriority())
                .build();
    }

    public static RoleDTO toDto(RoleEntity entity) {
        if (entity == null) return null;
        return RoleDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .priority(entity.getPriority())
                .build();
    }
}
