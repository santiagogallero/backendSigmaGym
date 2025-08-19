package com.sigma.gym.repository;

import com.sigma.gym.entity.ExerciseLibraryEntity;
import com.sigma.gym.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseLibraryRepository extends JpaRepository<ExerciseLibraryEntity, Long> {
    
    // Buscar ejercicios activos
    List<ExerciseLibraryEntity> findByIsActiveTrue();
    
    // Buscar por categoría
    List<ExerciseLibraryEntity> findByCategoryAndIsActiveTrue(ExerciseLibraryEntity.ExerciseCategory category);
    
    // Buscar por nivel de dificultad
    List<ExerciseLibraryEntity> findByDifficultyLevelAndIsActiveTrue(ExerciseLibraryEntity.DifficultyLevel difficultyLevel);
    
    // Buscar por entrenador creador
    List<ExerciseLibraryEntity> findByCreatedByTrainerAndIsActiveTrue(UserEntity trainer);
    
    // Buscar por nombre (búsqueda parcial)
    @Query("SELECT e FROM ExerciseLibraryEntity e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%')) AND e.isActive = true")
    List<ExerciseLibraryEntity> findByNameContainingIgnoreCaseAndIsActiveTrue(@Param("name") String name);
    
    // Buscar por músculos objetivo
    @Query("SELECT e FROM ExerciseLibraryEntity e WHERE LOWER(e.targetMuscles) LIKE LOWER(CONCAT('%', :muscle, '%')) AND e.isActive = true")
    List<ExerciseLibraryEntity> findByTargetMusclesContainingIgnoreCaseAndIsActiveTrue(@Param("muscle") String muscle);
    
    // Buscar por equipamiento
    @Query("SELECT e FROM ExerciseLibraryEntity e WHERE LOWER(e.equipment) LIKE LOWER(CONCAT('%', :equipment, '%')) AND e.isActive = true")
    List<ExerciseLibraryEntity> findByEquipmentContainingIgnoreCaseAndIsActiveTrue(@Param("equipment") String equipment);
    
    // Búsqueda compleja con filtros múltiples
    @Query("SELECT e FROM ExerciseLibraryEntity e WHERE " +
           "(:category IS NULL OR e.category = :category) AND " +
           "(:difficulty IS NULL OR e.difficultyLevel = :difficulty) AND " +
           "(:trainer IS NULL OR e.createdByTrainer = :trainer) AND " +
           "(:name IS NULL OR LOWER(e.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "e.isActive = true")
    Page<ExerciseLibraryEntity> findExercisesWithFilters(
        @Param("category") ExerciseLibraryEntity.ExerciseCategory category,
        @Param("difficulty") ExerciseLibraryEntity.DifficultyLevel difficulty,
        @Param("trainer") UserEntity trainer,
        @Param("name") String name,
        Pageable pageable
    );
    
    // Contar ejercicios por entrenador
    Long countByCreatedByTrainerAndIsActiveTrue(UserEntity trainer);
    
    // Obtener ejercicios más asignados
    @Query("SELECT e FROM ExerciseLibraryEntity e JOIN e.assignedExercises ae " +
           "WHERE e.isActive = true GROUP BY e ORDER BY COUNT(ae) DESC")
    List<ExerciseLibraryEntity> findMostAssignedExercises(Pageable pageable);
}
