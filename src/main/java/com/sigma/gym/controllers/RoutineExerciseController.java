package com.sigma.gym.controllers;

import com.sigma.gym.model.RoutineExercise;
import com.sigma.gym.services.RoutineExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routine-exercises")
public class RoutineExerciseController {

    @Autowired
    private RoutineExerciseService routineExerciseService;

    @PostMapping
    public ResponseEntity<RoutineExercise> addRoutineExercise(@RequestBody RoutineExercise routineExercise) {
        return ResponseEntity.ok(routineExerciseService.addRoutineExercise(routineExercise));
    }

    @GetMapping("/routine/{routineId}")
    public ResponseEntity<List<RoutineExercise>> getExercisesByRoutine(@PathVariable Long routineId) {
        return ResponseEntity.ok(routineExerciseService.getExercisesByRoutine(routineId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoutineExercise> updateRoutineExercise(
            @PathVariable Long id, @RequestBody RoutineExercise routineExercise) {
        return ResponseEntity.ok(routineExerciseService.updateRoutineExercise(id, routineExercise));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoutineExercise(@PathVariable Long id) {
        routineExerciseService.deleteRoutineExercise(id);
        return ResponseEntity.noContent().build();
    }
}
