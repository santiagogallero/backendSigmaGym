package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.AttendanceDTO;
import com.sigma.gym.entity.AttendanceEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.model.Attendance;

public class AttendanceMapper {

    // Entity → Model
    public static Attendance toModel(AttendanceEntity entity) {
        if (entity == null) return null;

        Attendance attendance = new Attendance();
        attendance.setId(entity.getId());
        attendance.setDate(entity.getDate());
        attendance.setStatus(entity.getStatus());
        attendance.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        attendance.setCheckInTime(entity.getCheckInTime());
        attendance.setCheckOutTime(entity.getCheckOutTime());
        return attendance;
    }

    // Model → Entity
    public static AttendanceEntity toEntity(Attendance model) {
        if (model == null) return null;

        AttendanceEntity entity = new AttendanceEntity();
        entity.setId(model.getId());
        entity.setDate(model.getDate());
        entity.setStatus(model.getStatus());

        if (model.getUserId() != null) {
            UserEntity user = new UserEntity();
            user.setId(model.getUserId());
            entity.setUser(user);
        }

        entity.setCheckInTime(model.getCheckInTime());
        entity.setCheckOutTime(model.getCheckOutTime());
        return entity;
    }

    // Model → DTO
    public static AttendanceDTO toDto(Attendance model) {
        if (model == null) return null;

        return AttendanceDTO.builder()
                .id(model.getId())
                .date(model.getDate())
                .status(model.getStatus())
                .userId(model.getUserId())
                .checkInTime(model.getCheckInTime())
                .checkOutTime(model.getCheckOutTime())
                .build();
    }

    // DTO → Model
    public static Attendance toModel(AttendanceDTO dto) {
        if (dto == null) return null;

        Attendance attendance = new Attendance();
        attendance.setId(dto.getId());
        attendance.setDate(dto.getDate());
        attendance.setStatus(dto.getStatus());
        attendance.setUserId(dto.getUserId());
        attendance.setCheckInTime(dto.getCheckInTime());
        attendance.setCheckOutTime(dto.getCheckOutTime());
        return attendance;
    }
}
