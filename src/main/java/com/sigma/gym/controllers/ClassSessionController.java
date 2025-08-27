package com.sigma.gym.controllers;

import com.sigma.gym.DTOs.ClassSessionDTO;
import com.sigma.gym.DTOs.CreateClassSessionRequestDTO;
import com.sigma.gym.DTOs.UpdateClassSessionRequestDTO;
import com.sigma.gym.services.ClassSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v2/classes")
@RequiredArgsConstructor
public class ClassSessionController {

    private final ClassSessionService classSessionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_TRAINER','ROLE_OWNER','ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<ClassSessionDTO> create(@Valid @RequestBody CreateClassSessionRequestDTO request) {
        log.info("Creating class {} - trainer {}", request.getClassName(), request.getTrainerId());
        return ResponseEntity.ok(classSessionService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TRAINER','ROLE_OWNER','ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<ClassSessionDTO> update(@PathVariable Long id, @Valid @RequestBody UpdateClassSessionRequestDTO request) {
        log.info("Updating class {}", id);
        return ResponseEntity.ok(classSessionService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_TRAINER','ROLE_OWNER','ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        log.info("Deactivating class {}", id);
        classSessionService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    // Turnera simple: listar clases disponibles para miembros
    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER','ROLE_TRAINER','ROLE_OWNER')")
    public ResponseEntity<java.util.List<ClassSessionDTO>> listAvailable() {
        return ResponseEntity.ok(classSessionService.listAvailable());
    }

    // Próximas clases (paginado básico)
    @GetMapping("/upcoming")
    @PreAuthorize("hasAnyRole('ROLE_MEMBER','ROLE_TRAINER','ROLE_OWNER')")
    public ResponseEntity<java.util.List<ClassSessionDTO>> listUpcoming(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(classSessionService.listUpcoming(page, size));
    }

    // Clases de un trainer para su gestión
    @GetMapping("/trainer/{trainerId}")
    @PreAuthorize("hasAnyRole('ROLE_TRAINER','ROLE_OWNER','ROLE_ADMIN','ROLE_STAFF')")
    public ResponseEntity<java.util.List<ClassSessionDTO>> listByTrainer(@PathVariable Long trainerId) {
        return ResponseEntity.ok(classSessionService.listByTrainer(trainerId));
    }
}
