package com.sigma.gym.mappers;

import com.sigma.gym.entity.ProgressEntity;
import com.sigma.gym.entity.RoutineProgressEntity;
import com.sigma.gym.model.RoutineProgress;

import java.util.List;
import java.util.stream.Collectors;

public class RoutineProgressMapper {

    // Entity → Domain
    public static RoutineProgress toModel(RoutineProgressEntity entity) {
        if (entity == null) return null;

        return RoutineProgress.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .routineId(entity.getRoutine() != null ? entity.getRoutine().getId() : null)
                .completedDays(entity.getCompletedDays())
                .startDate(entity.getStartDate())
                .lastUpdate(entity.getLastUpdate())
                .isCompleted(entity.isCompleted())
                .progressId(entity.getProgress() != null ? entity.getProgress().getId() : null)
                .build();
    }
public static RoutineProgressEntity toEntity(RoutineProgress model) {
    if (model == null) return null;

    RoutineProgressEntity entity = RoutineProgressEntity.builder()
            .id(model.getId())
            .completedDays(model.getCompletedDays())
            .startDate(model.getStartDate())
            .lastUpdate(model.getLastUpdate())
            .isCompleted(model.isCompleted())
            .build();

    if (model.getProgressId() != null) {
        ProgressEntity progressEntity = new ProgressEntity();
        progressEntity.setId(model.getProgressId());
        entity.setProgress(progressEntity); // <-- cambio
    }

    return entity;
}

    // List<Entity> → List<Domain>
    public static List<RoutineProgress> toModelList(List<RoutineProgressEntity> entities) {
        return entities == null ? List.of()
                : entities.stream().map(RoutineProgressMapper::toModel).collect(Collectors.toList());
    }

    // List<Domain> → List<Entity>
    public static List<RoutineProgressEntity> toEntityList(List<RoutineProgress> models) {
        return models == null ? List.of()
                : models.stream().map(RoutineProgressMapper::toEntity).collect(Collectors.toList());
    }
}
