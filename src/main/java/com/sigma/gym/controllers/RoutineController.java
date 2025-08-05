package com.sigma.gym.controllers;

import com.sigma.gym.model.Routine;
import com.sigma.gym.services.RoutineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routines")
public class RoutineController {

    @Autowired
    private RoutineService routineService;

    @PostMapping
    public ResponseEntity<Routine> createRoutine(@RequestBody Routine routine) {
        return ResponseEntity.ok(routineService.createRoutine(routine));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Routine> getRoutineById(@PathVariable Long id) {
        return ResponseEntity.ok(routineService.getRoutineById(id));
    }

    @GetMapping
    public ResponseEntity<List<Routine>> getAllRoutines() {
        return ResponseEntity.ok(routineService.getAllRoutines());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Routine> updateRoutine(@PathVariable Long id, @RequestBody Routine routine) {
        return ResponseEntity.ok(routineService.updateRoutine(id, routine));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutine(@PathVariable Long id) {
        routineService.deleteRoutine(id);
        return ResponseEntity.noContent().build();
    }
}
