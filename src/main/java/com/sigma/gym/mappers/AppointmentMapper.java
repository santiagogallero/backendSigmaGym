package com.sigma.gym.mappers;

import com.sigma.gym.DTOs.AppointmentDTO;
import com.sigma.gym.entity.AppointmentEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.model.Appointment;

public class AppointmentMapper {

    public static Appointment toModel(AppointmentEntity entity) {
        if (entity == null) return null;

        Appointment appointment = new Appointment();
        appointment.setId(entity.getId());
        appointment.setUserId(entity.getUserId());
        appointment.setTrainerId(entity.getTrainerId());
        appointment.setDate(entity.getDate());
        appointment.setStatus(entity.getStatus());
        return appointment;
    }

    public static AppointmentEntity toEntity(Appointment model) {
        if (model == null) return null;

        AppointmentEntity entity = new AppointmentEntity();
        entity.setId(model.getId());
        UserEntity user = new UserEntity();
        user.setId(model.getUserId());
        entity.setUser(user);

        UserEntity trainer = new UserEntity();
        trainer.setId(model.getTrainerId());
        entity.setTrainer(trainer);

        entity.setDate(model.getDate());
        entity.setStatus(model.getStatus());
        return entity;
    }

    public static AppointmentDTO toDto(Appointment model) {
        if (model == null) return null;

        return AppointmentDTO.builder()
                .id(model.getId())
                .userId(model.getUserId())
                .trainerId(model.getTrainerId())
                .date(model.getDate())
                .status(model.getStatus())
                .build();
    }

    public static Appointment toModel(AppointmentDTO dto) {
        if (dto == null) return null;

        Appointment appointment = new Appointment();
        appointment.setId(dto.getId());
        appointment.setUserId(dto.getUserId());
        appointment.setTrainerId(dto.getTrainerId());
        appointment.setDate(dto.getDate());
        appointment.setStatus(dto.getStatus());
        return appointment;
    }
}
