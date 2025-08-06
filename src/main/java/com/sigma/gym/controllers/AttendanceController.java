package com.sigma.gym.controllers;

import com.sigma.gym.DTOs.AttendanceDTO;
import com.sigma.gym.mappers.AttendanceMapper;
import com.sigma.gym.model.Attendance;
import com.sigma.gym.services.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService service;

    public AttendanceController(AttendanceService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AttendanceDTO> create(@RequestBody AttendanceDTO dto) {
        Attendance created = service.create(AttendanceMapper.toModel(dto));
        return ResponseEntity.ok(AttendanceMapper.toDto(created));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                AttendanceMapper.toDto(service.getById(id))
        );
    }

    @GetMapping
    public ResponseEntity<List<AttendanceDTO>> getAll() {
        return ResponseEntity.ok(
                service.getAll().stream()
                        .map(AttendanceMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<AttendanceDTO> update(@PathVariable Long id, @RequestBody AttendanceDTO dto) {
        Attendance updated = service.update(id, AttendanceMapper.toModel(dto));
        return ResponseEntity.ok(AttendanceMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AttendanceDTO>> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(
                service.getByUserId(userId).stream()
                        .map(AttendanceMapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<AttendanceDTO>> getByDate(@PathVariable LocalDate date) {
        return ResponseEntity.ok(
                service.getByDate(date).stream()
                        .map(AttendanceMapper::toDto)
                        .collect(Collectors.toList())
        );
    }
}
