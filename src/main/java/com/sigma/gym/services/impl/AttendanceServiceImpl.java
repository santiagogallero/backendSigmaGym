package com.sigma.gym.services.impl;

import com.sigma.gym.entity.AttendanceEntity;
import com.sigma.gym.exceptions.ResourceNotFoundException;
import com.sigma.gym.mappers.AttendanceMapper;
import com.sigma.gym.model.Attendance;
import com.sigma.gym.repository.AttendanceRepository;
import com.sigma.gym.services.AttendanceService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository repository;

    public AttendanceServiceImpl(AttendanceRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Attendance create(Attendance attendance) {
        AttendanceEntity entity = AttendanceMapper.toEntity(attendance);
        return AttendanceMapper.toModel(repository.save(entity));
    }

    @Override
    public Attendance getById(Long id) {
        AttendanceEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));
        return AttendanceMapper.toModel(entity);
    }

    @Override
    public List<Attendance> getAll() {
        return repository.findAll()
                .stream()
                .map(AttendanceMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Attendance update(Long id, Attendance attendance) {
        AttendanceEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance not found with id: " + id));

        entity.setDate(attendance.getDate());
        entity.setStatus(attendance.getStatus());
        entity.setCheckInTime(attendance.getCheckInTime());
        entity.setCheckOutTime(attendance.getCheckOutTime());

        return AttendanceMapper.toModel(repository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Attendance not found with id: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public List<Attendance> getByUserId(Long userId) {
        return repository.findByUser_Id(userId)
                .stream()
                .map(AttendanceMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<Attendance> getByDate(LocalDate date) {
        return repository.findByDate(date)
                .stream()
                .map(AttendanceMapper::toModel)
                .collect(Collectors.toList());
    }
}
