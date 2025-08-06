package com.sigma.gym.controllers;

import com.sigma.gym.DTOs.AppointmentDTO;
import com.sigma.gym.mappers.AppointmentMapper;
import com.sigma.gym.model.Appointment;
import com.sigma.gym.services.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AppointmentDTO> create(@RequestBody AppointmentDTO dto) {
        Appointment created = service.create(AppointmentMapper.toModel(dto));
        return ResponseEntity.ok(AppointmentMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                AppointmentMapper.toDto(service.getById(id))
        );
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAll() {
        return ResponseEntity.ok(
                service.getAll().stream()
                        .map(AppointmentMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> update(@PathVariable Long id, @RequestBody AppointmentDTO dto) {
        Appointment updated = service.update(id, AppointmentMapper.toModel(dto));
        return ResponseEntity.ok(AppointmentMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AppointmentDTO>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(
                service.getByUserId(userId).stream()
                        .map(AppointmentMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/trainer/{trainerId}")
    public ResponseEntity<List<AppointmentDTO>> getByTrainerId(@PathVariable Long trainerId) {
        return ResponseEntity.ok(
                service.getByTrainerId(trainerId).stream()
                        .map(AppointmentMapper::toDto)
                        .collect(Collectors.toList())
        );
    }
}
