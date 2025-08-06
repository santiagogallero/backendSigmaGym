package com.sigma.gym.services.impl;

import com.sigma.gym.entity.ProgressEntity;
import com.sigma.gym.entity.RoutineProgressEntity;
import com.sigma.gym.exceptions.ResourceNotFoundException;
import com.sigma.gym.mappers.RoutineProgressMapper;
import com.sigma.gym.model.RoutineProgress;
import com.sigma.gym.repository.RoutineProgressRepository;
import com.sigma.gym.services.RoutineProgressService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoutineProgressServiceImpl implements RoutineProgressService {

    @Autowired
    private RoutineProgressRepository repository;

    @Override
    @Transactional
    public RoutineProgress createRoutineProgress(RoutineProgress progress) {
        RoutineProgressEntity entity = RoutineProgressMapper.toEntity(progress);
        return RoutineProgressMapper.toModel(repository.save(entity));
    }

    @Override
    public RoutineProgress getRoutineProgressById(Long id) {
        return RoutineProgressMapper.toModel(
                repository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("RoutineProgress not found with id: " + id))
        );
    }

    @Override
    public List<RoutineProgress> getRoutineProgressByUser(Long userId) {
        return repository.findByUser_Id(userId).stream()
                .map(RoutineProgressMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoutineProgress> getRoutineProgressByRoutine(Long routineId) {
        return repository.findByRoutine_Id(routineId).stream()
                .map(RoutineProgressMapper::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoutineProgress updateRoutineProgress(Long id, RoutineProgress progress) {
        RoutineProgressEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoutineProgress not found with id: " + id));

        entity.setCompletedDays(progress.getCompletedDays());
        entity.setStartDate(progress.getStartDate());
        entity.setLastUpdate(progress.getLastUpdate());
                entity.setCompleted(progress.isCompleted());
        if (progress.getProgressId() != null) {
            ProgressEntity progressEntity = new ProgressEntity();
            progressEntity.setId(progress.getProgressId());
            entity.setProgress(progressEntity);
}



        return RoutineProgressMapper.toModel(repository.save(entity));
    }

    @Override
    @Transactional
    public void deleteRoutineProgress(Long id) {
        RoutineProgressEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RoutineProgress not found with id: " + id));
        repository.delete(entity);
    }
}
