package com.sigma.gym.services.impl;

import com.sigma.gym.DTOs.WorkoutPlanDTO;
import com.sigma.gym.DTOs.WorkoutPlanStatsDTO;
import com.sigma.gym.entity.WorkoutPlanEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.entity.PlanDayEntity;
import com.sigma.gym.mappers.WorkoutPlanMapper;
import com.sigma.gym.model.WorkoutPlan;
import com.sigma.gym.model.WorkoutPlanStatus;
import com.sigma.gym.repository.UserRepository;
import com.sigma.gym.repository.WorkoutPlanRepository;
import com.sigma.gym.repository.PlanDayRepository;
import com.sigma.gym.repository.PlanExerciseRepository;
import com.sigma.gym.services.WorkoutPlanService;
import com.sigma.gym.utils.SlugUtils;
import com.sigma.gym.utils.ReorderUtils;
import com.sigma.gym.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class WorkoutPlanServiceImpl implements WorkoutPlanService {

    @Autowired
    private WorkoutPlanRepository workoutPlanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanDayRepository planDayRepository;

    @Autowired
    private PlanExerciseRepository planExerciseRepository;

    @Autowired
    private SlugUtils slugUtils;

    @Autowired
    private ReorderUtils reorderUtils;

    @Override
    public WorkoutPlan createWorkoutPlan(WorkoutPlan workoutPlan) {
        WorkoutPlanEntity entity = WorkoutPlanMapper.toEntity(workoutPlan);

        // Mapear trainer (User)
        if (workoutPlan.getTrainer() != null && workoutPlan.getTrainer().getId() != null) {
            UserEntity trainer = userRepository.findById(workoutPlan.getTrainer().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + workoutPlan.getTrainer().getId()));
            entity.setTrainer(trainer);
        }

        return WorkoutPlanMapper.toDomain(workoutPlanRepository.save(entity));
    }

    @Override
    public WorkoutPlan getWorkoutPlanById(Long id) {
        WorkoutPlanEntity entity = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan not found with id: " + id));
        return WorkoutPlanMapper.toDomain(entity);
    }

    @Override
    public List<WorkoutPlan> getAllWorkoutPlans() {
        return workoutPlanRepository.findAll()
                .stream()
                .map(WorkoutPlanMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public WorkoutPlan updateWorkoutPlan(Long id, WorkoutPlan workoutPlan) {
        WorkoutPlanEntity entity = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan not found with id: " + id));

        entity.setName(workoutPlan.getName());
        entity.setGoal(workoutPlan.getGoal());
        entity.setDescription(workoutPlan.getDescription());
        entity.setStartDate(workoutPlan.getStartDate());
        entity.setEndDate(workoutPlan.getEndDate());
        entity.setDifficulty(workoutPlan.getDifficulty());
        entity.setNotes(workoutPlan.getNotes());
        entity.setCreatedAt(workoutPlan.getCreatedAt());

        // Actualizar trainer si cambia
        if (workoutPlan.getTrainer() != null && workoutPlan.getTrainer().getId() != null) {
            UserEntity trainer = userRepository.findById(workoutPlan.getTrainer().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Trainer not found with id: " + workoutPlan.getTrainer().getId()));
            entity.setTrainer(trainer);
        }

        return WorkoutPlanMapper.toDomain(workoutPlanRepository.save(entity));
    }

    @Override
    public void deleteWorkoutPlan(Long id) {
        WorkoutPlanEntity entity = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("WorkoutPlan not found with id: " + id));
        workoutPlanRepository.delete(entity);
    }

    // ===== NEW ENHANCED METHODS =====

    @Override
    public WorkoutPlanEntity createWorkoutPlanEntity(Long ownerId, WorkoutPlanDTO workoutPlanDTO) {
        UserEntity owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Owner not found with id: " + ownerId));

        // Generate unique slug
        String slug = slugUtils.generateUniqueSlug(ownerId, workoutPlanDTO.getName());

        WorkoutPlanEntity entity = WorkoutPlanEntity.builder()
                .owner(owner)
                .name(workoutPlanDTO.getName())
                .slug(slug)
                .status(workoutPlanDTO.getStatus() != null ? workoutPlanDTO.getStatus() : WorkoutPlanStatus.DRAFT)
                .goal(workoutPlanDTO.getGoal())
                .description(workoutPlanDTO.getDescription())
                .startDate(workoutPlanDTO.getStartDate())
                .endDate(workoutPlanDTO.getEndDate())
                .difficulty(workoutPlanDTO.getDifficulty())
                .notes(workoutPlanDTO.getNotes())
                .createdAt(LocalDate.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return workoutPlanRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkoutPlanEntity> getWorkoutPlanBySlug(Long ownerId, String slug) {
        return workoutPlanRepository.findByOwner_IdAndSlug(ownerId, slug);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WorkoutPlanEntity> getWorkoutPlanEntityById(Long id) {
        return workoutPlanRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutPlanEntity> getWorkoutPlansByOwnerId(Long ownerId) {
        return workoutPlanRepository.findByOwner_Id(ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutPlanEntity> getWorkoutPlansByOwnerAndStatus(Long ownerId, WorkoutPlanStatus status) {
        return workoutPlanRepository.findByOwner_IdAndStatus(ownerId, status);
    }

    @Override
    public WorkoutPlanEntity updateWorkoutPlanEntity(Long id, WorkoutPlanDTO workoutPlanDTO) {
        WorkoutPlanEntity entity = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkoutPlan not found with id: " + id));

        // Check if plan can be edited
        if (entity.isActive() || entity.isArchived()) {
            throw new RuntimeException("Cannot modify active or archived workout plans");
        }

        // Update slug if name changed
        if (workoutPlanDTO.getName() != null && !workoutPlanDTO.getName().equals(entity.getName())) {
            String newSlug = slugUtils.generateUniqueSlugForUpdate(
                entity.getOwnerId(), 
                workoutPlanDTO.getName(), 
                entity.getSlug()
            );
            entity.setSlug(newSlug);
            entity.setName(workoutPlanDTO.getName());
        }

        // Update other fields
        if (workoutPlanDTO.getGoal() != null) {
            entity.setGoal(workoutPlanDTO.getGoal());
        }
        if (workoutPlanDTO.getDescription() != null) {
            entity.setDescription(workoutPlanDTO.getDescription());
        }
        if (workoutPlanDTO.getStartDate() != null) {
            entity.setStartDate(workoutPlanDTO.getStartDate());
        }
        if (workoutPlanDTO.getEndDate() != null) {
            entity.setEndDate(workoutPlanDTO.getEndDate());
        }
        if (workoutPlanDTO.getDifficulty() != null) {
            entity.setDifficulty(workoutPlanDTO.getDifficulty());
        }
        if (workoutPlanDTO.getNotes() != null) {
            entity.setNotes(workoutPlanDTO.getNotes());
        }

        return workoutPlanRepository.save(entity);
    }

    @Override
    public WorkoutPlanEntity partialUpdateWorkoutPlan(Long id, WorkoutPlanDTO workoutPlanDTO) {
        // Same as updateWorkoutPlanEntity for now
        return updateWorkoutPlanEntity(id, workoutPlanDTO);
    }

    @Override
    public void deleteWorkoutPlanEntity(Long id) {
        WorkoutPlanEntity entity = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkoutPlan not found with id: " + id));
        workoutPlanRepository.delete(entity);
    }

    @Override
    public WorkoutPlanEntity changeStatus(Long id, WorkoutPlanStatus newStatus) {
        WorkoutPlanEntity entity = workoutPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("WorkoutPlan not found with id: " + id));

        entity.setStatus(newStatus);
        return workoutPlanRepository.save(entity);
    }

    @Override
    public WorkoutPlanEntity duplicateWorkoutPlan(Long originalId, String newName) {
        WorkoutPlanEntity original = workoutPlanRepository.findById(originalId)
                .orElseThrow(() -> new RuntimeException("WorkoutPlan not found with id: " + originalId));

        String newSlug = slugUtils.generateUniqueSlug(original.getOwnerId(), newName);

        WorkoutPlanEntity duplicate = WorkoutPlanEntity.builder()
                .owner(original.getOwner())
                .name(newName)
                .slug(newSlug)
                .status(WorkoutPlanStatus.DRAFT)
                .goal(original.getGoal())
                .description(original.getDescription())
                .startDate(original.getStartDate())
                .endDate(original.getEndDate())
                .difficulty(original.getDifficulty())
                .notes(original.getNotes())
                .createdAt(LocalDate.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return workoutPlanRepository.save(duplicate);
        // Note: Days and exercises would need to be duplicated separately
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkoutPlanEntity> searchWorkoutPlansByName(Long ownerId, String name) {
        return workoutPlanRepository.findByOwner_IdAndNameContainingIgnoreCase(ownerId, name);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canEditWorkoutPlan(Long planId, Long userId) {
        WorkoutPlanEntity entity = workoutPlanRepository.findById(planId)
                .orElse(null);
        
        if (entity == null) {
            return false;
        }

        // Only owner can edit and only in DRAFT status
        return entity.getOwnerId().equals(userId) && entity.isDraft();
    }

    @Override
    public void validateAndFixWorkoutPlan(Long planId) {
        // Validate and fix order indexes for all days and exercises
        List<PlanDayEntity> days = planDayRepository.findByWorkoutPlanIdOrderByOrderIndexAsc(planId);
        for (PlanDayEntity day : days) {
            if (!reorderUtils.validatePlanExerciseOrder(day.getId())) {
                reorderUtils.fixPlanExerciseOrder(day.getId());
            }
        }
        // Fix day order as well
        if (!reorderUtils.validatePlanDayOrder(planId)) {
            reorderUtils.fixPlanDayOrder(planId);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public WorkoutPlanStatsDTO getWorkoutPlanStats(Long planId) {
        WorkoutPlanEntity entity = workoutPlanRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("WorkoutPlan not found with id: " + planId));

        var days = planDayRepository.findByWorkoutPlanIdOrderByOrderIndexAsc(planId);
        
        int totalExercises = 0;
        int warmupExercises = 0;
        int minExercisesPerDay = Integer.MAX_VALUE;
        int maxExercisesPerDay = 0;
        int activeDays = 0;

        for (var day : days) {
            var exercises = planExerciseRepository.findByPlanDay_IdOrderByOrderIndexAsc(day.getId());
            int dayExerciseCount = exercises.size();
            
            if (dayExerciseCount > 0) {
                activeDays++;
                totalExercises += dayExerciseCount;
                minExercisesPerDay = Math.min(minExercisesPerDay, dayExerciseCount);
                maxExercisesPerDay = Math.max(maxExercisesPerDay, dayExerciseCount);
                
                warmupExercises += (int) exercises.stream().filter(e -> Boolean.TRUE.equals(e.getIsWarmup())).count();
            }
        }

        if (activeDays == 0) {
            minExercisesPerDay = 0;
        }

        double avgExercisesPerDay = activeDays > 0 ? (double) totalExercises / activeDays : 0.0;

        Integer estimatedDurationDays = null;
        if (entity.getStartDate() != null && entity.getEndDate() != null) {
            estimatedDurationDays = (int) ChronoUnit.DAYS.between(entity.getStartDate(), entity.getEndDate()) + 1;
        }

        return WorkoutPlanStatsDTO.builder()
                .planId(entity.getId())
                .planName(entity.getName())
                .planSlug(entity.getSlug())
                .totalDays(days.size())
                .activeDays(activeDays)
                .totalExercises(totalExercises)
                .warmupExercises(warmupExercises)
                .mainExercises(totalExercises - warmupExercises)
                .minExercisesPerDay(minExercisesPerDay)
                .maxExercisesPerDay(maxExercisesPerDay)
                .avgExercisesPerDay(avgExercisesPerDay)
                .totalMembers(entity.hasMembers() ? entity.getMembers().size() : 0)
                .hasMembers(entity.hasMembers())
                .estimatedDurationDays(estimatedDurationDays)
                .isComplete(activeDays > 0 && totalExercises > 0)
                .isValid(true) // Would need more complex validation logic
                .build();
    }
}
