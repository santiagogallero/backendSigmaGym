package com.sigma.gym.services;

import com.sigma.gym.model.Appointment;
import com.sigma.gym.model.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentService {
    Appointment create(Appointment appointment);
    Appointment getById(Long id);
    List<Appointment> getAll();
    Appointment update(Long id, Appointment appointment);
    void delete(Long id);
    List<Appointment> getByUserId(Long userId);
    List<Appointment> getByTrainerId(Long trainerId);
    
    // Admin methods
    List<Appointment> getByDateTimeRange(LocalDateTime from, LocalDateTime to);
    Appointment partialUpdate(Long id, LocalDateTime date, AppointmentStatus status);
}
