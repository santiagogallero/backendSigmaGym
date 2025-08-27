package com.sigma.gym.services;

import com.sigma.gym.DTOs.ClassSessionDTO;
import com.sigma.gym.DTOs.CreateClassSessionRequestDTO;
import com.sigma.gym.DTOs.UpdateClassSessionRequestDTO;
import com.sigma.gym.entity.ClassSessionEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.exceptions.ClassSessionNotFoundException;
import com.sigma.gym.exceptions.ValidationException;
import com.sigma.gym.repository.ClassSessionRepository;
import com.sigma.gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassSessionService {

    private final ClassSessionRepository classSessionRepository;
    private final UserRepository userRepository;

    @Transactional
    public ClassSessionDTO create(CreateClassSessionRequestDTO req) {
        validateTimes(req.getStartsAt(), req.getEndsAt());
        UserEntity trainer = userRepository.findById(req.getTrainerId())
            .orElseThrow(() -> new ValidationException("Trainer not found"));

        ClassSessionEntity entity = ClassSessionEntity.builder()
            .className(req.getClassName())
            .classType(req.getClassType())
            .description(req.getDescription())
            .trainer(trainer)
            .programId(req.getProgramId())
            .coachId(req.getCoachId())
            .locationId(req.getLocationId())
            .startsAt(req.getStartsAt())
            .endsAt(req.getEndsAt())
            .capacity(req.getCapacity())
            .isActive(true)
            .build();

        entity = classSessionRepository.save(entity);
        log.info("Class session {} created by trainer {}", entity.getId(), trainer.getId());
        return ClassSessionDTO.fromEntity(entity);
    }

    @Transactional
    public ClassSessionDTO update(Long id, UpdateClassSessionRequestDTO req) {
        ClassSessionEntity entity = classSessionRepository.findByIdForUpdate(id)
            .orElseThrow(() -> new ClassSessionNotFoundException(id));

        if (req.getStartsAt() != null && req.getEndsAt() != null) {
            validateTimes(req.getStartsAt(), req.getEndsAt());
        }

        if (req.getCapacity() != null && req.getCapacity() < entity.getBookedCount()) {
            throw new ValidationException("Capacity cannot be less than booked count");
        }

        if (req.getClassName() != null) entity.setClassName(req.getClassName());
        if (req.getClassType() != null) entity.setClassType(req.getClassType());
        if (req.getDescription() != null) entity.setDescription(req.getDescription());
        if (req.getStartsAt() != null) entity.setStartsAt(req.getStartsAt());
        if (req.getEndsAt() != null) entity.setEndsAt(req.getEndsAt());
        if (req.getCapacity() != null) entity.setCapacity(req.getCapacity());
        if (req.getIsActive() != null) entity.setIsActive(req.getIsActive());

        entity = classSessionRepository.save(entity);
        return ClassSessionDTO.fromEntity(entity);
    }

    @Transactional
    public void deactivate(Long id) {
        ClassSessionEntity entity = classSessionRepository.findByIdForUpdate(id)
            .orElseThrow(() -> new ClassSessionNotFoundException(id));
        entity.setIsActive(false);
        classSessionRepository.save(entity);
        log.info("Class session {} deactivated", id);
    }

    private void validateTimes(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) {
            throw new ValidationException("Invalid time range");
        }
    }

    @Transactional(readOnly = true)
    public List<ClassSessionDTO> listAvailable() {
        return classSessionRepository.findAvailableClasses(LocalDateTime.now())
            .stream()
            .map(ClassSessionDTO::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClassSessionDTO> listUpcoming(int page, int size) {
        return classSessionRepository.findUpcomingClasses(LocalDateTime.now(), org.springframework.data.domain.PageRequest.of(page, size))
            .map(ClassSessionDTO::fromEntity)
            .getContent();
    }

    @Transactional(readOnly = true)
    public List<ClassSessionDTO> listByTrainer(Long trainerId) {
        return classSessionRepository.findByTrainerIdAndIsActiveTrue(trainerId)
            .stream()
            .map(ClassSessionDTO::fromEntity)
            .collect(Collectors.toList());
    }
}
