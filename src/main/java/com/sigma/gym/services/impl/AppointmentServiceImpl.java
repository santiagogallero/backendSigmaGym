package com.sigma.gym.services.impl;

import com.sigma.gym.entity.AppointmentEntity;
import com.sigma.gym.exceptions.ResourceNotFoundException;
import com.sigma.gym.mappers.AppointmentMapper;
import com.sigma.gym.model.Appointment;
import com.sigma.gym.model.AppointmentStatus;
import com.sigma.gym.repository.AppointmentRepository;
import com.sigma.gym.services.AppointmentService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository repository;

    public AppointmentServiceImpl(AppointmentRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Appointment create(Appointment appointment) {
        AppointmentEntity entity = AppointmentMapper.toEntity(appointment);
        return AppointmentMapper.toModel(repository.save(entity));
    }

    @Override
    public Appointment getById(Long id) {
        AppointmentEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        return AppointmentMapper.toModel(entity);
    }

    @Override
    public List<Appointment> getAll() {
        return repository.findAll()
                .stream()
                .map(AppointmentMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Appointment update(Long id, Appointment appointment) {
        AppointmentEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));

        entity.setDate(appointment.getDate());
        entity.setStatus(appointment.getStatus());
        // user y trainer no se cambian aquí para evitar inconsistencias

        return AppointmentMapper.toModel(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Appointment not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<Appointment> getByUserId(Long userId) {
        return repository.findByUser_Id(userId)
                .stream()
                .map(AppointmentMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> getByTrainerId(Long trainerId) {
        return repository.findByTrainer_Id(trainerId)
                .stream()
                .map(AppointmentMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Appointment> getByDateTimeRange(LocalDateTime from, LocalDateTime to) {
        return repository.findByDateBetween(from, to).stream()
                .map(AppointmentMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Appointment partialUpdate(Long id, LocalDateTime date, AppointmentStatus status) {
        AppointmentEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with id: " + id));
        
        if (date != null) {
            entity.setDate(date);
        }
        if (status != null) {
            entity.setStatus(status);
        }
        
        return AppointmentMapper.toModel(repository.save(entity));
    }
}
