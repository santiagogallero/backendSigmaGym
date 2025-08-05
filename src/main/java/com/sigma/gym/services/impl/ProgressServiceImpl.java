package com.sigma.gym.services.impl;

import com.sigma.gym.entity.ProgressEntity;
import com.sigma.gym.mappers.ProgressMapper;
import com.sigma.gym.model.Progress;
import com.sigma.gym.repository.ProgressRepository;
import com.sigma.gym.services.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProgressServiceImpl implements ProgressService {

    @Autowired
    private ProgressRepository progressRepository;

    @Override
    public Progress createProgress(Progress progress) {
        ProgressEntity entity = ProgressMapper.toEntity(progress);
        return ProgressMapper.toDomain(progressRepository.save(entity));
    }

    @Override
    public Progress getProgressById(Long id) {
        ProgressEntity entity = progressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Progress not found with id: " + id));
        return ProgressMapper.toDomain(entity);
    }

    @Override
    public List<Progress> getAllProgress() {
        return progressRepository.findAll()
                .stream()
                .map(ProgressMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Progress updateProgress(Long id, Progress progress) {
        ProgressEntity entity = progressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Progress not found with id: " + id));

        entity.setId(progress.getUserId());
        entity.setDate(progress.getDate());
        entity.setWeight(progress.getWeight());
        entity.setNotes(progress.getNotes());
        entity.setMuscleMass(progress.getMuscleMass());
        entity.setBodyFatPercentage(progress.getBodyFatPercentage());

        return ProgressMapper.toDomain(progressRepository.save(entity));
    }

    @Override
    public void deleteProgress(Long id) {
        ProgressEntity entity = progressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Progress not found with id: " + id));
        progressRepository.delete(entity);
    }
}
