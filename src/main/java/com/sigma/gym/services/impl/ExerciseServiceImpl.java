package com.sigma.gym.services.impl;

import com.sigma.gym.entity.ExerciseEntity;
import com.sigma.gym.mappers.ExerciseMapper;
import com.sigma.gym.mappers.UserMapper;
import com.sigma.gym.model.Exercise;
import com.sigma.gym.repository.ExerciseRepository;
import com.sigma.gym.services.ExerciseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

@Service
public class ExerciseServiceImpl implements ExerciseService {

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Override
    public Exercise createExercise(Exercise exercise) {
        ExerciseEntity entity = ExerciseMapper.toEntity(exercise);
        return ExerciseMapper.toDomain(exerciseRepository.save(entity));
    }

    @Override
    public Exercise getExerciseById(Long id) {
        ExerciseEntity entity = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + id));
        return ExerciseMapper.toDomain(entity);
    }

    @Override
    public List<Exercise> getAllExercises() {
        return exerciseRepository.findAll()
                .stream()
                .map(ExerciseMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Exercise updateExercise(Long id, Exercise exercise) {
        ExerciseEntity entity = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + id));

        // Actualizar campos
        entity.setName(exercise.getName());
        entity.setDescription(exercise.getDescription());
        entity.setCategory(exercise.getCategory());
        entity.setEquipment(exercise.getEquipment());
        entity.setDuration(exercise.getDuration());
        entity.setSets(exercise.getSets());
        entity.setReps(exercise.getReps());
        entity.setVideoUrl(exercise.getVideoUrl());
        entity.setCreatedBy(UserMapper.toEntity(exercise.getCreatedBy())); // si tu mapper lo soporta

        return ExerciseMapper.toDomain(exerciseRepository.save(entity));
    }

    @Override
    public void deleteExercise(Long id) {
        ExerciseEntity entity = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise not found with id: " + id));
        exerciseRepository.delete(entity);
    }
}
