package com.sigma.gym.repository;

import com.sigma.gym.entity.AssignedExerciseEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.entity.ExerciseLibraryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AssignedExerciseRepository extends JpaRepository<AssignedExerciseEntity, Long> {
    
    // Obtener ejercicios asignados a un usuario
    List<AssignedExerciseEntity> findByAssignedToUser(UserEntity user);
    
    // Obtener ejercicios asignados por un entrenador
    List<AssignedExerciseEntity> findByAssignedByTrainer(UserEntity trainer);
    
    // Obtener ejercicios por estado
    List<AssignedExerciseEntity> findByStatus(AssignedExerciseEntity.AssignmentStatus status);
    
    // Obtener ejercicios asignados a un usuario con estado específico
    List<AssignedExerciseEntity> findByAssignedToUserAndStatus(
        UserEntity user, 
        AssignedExerciseEntity.AssignmentStatus status
    );
    
    // Obtener ejercicios asignados por un entrenador a un usuario específico
    List<AssignedExerciseEntity> findByAssignedByTrainerAndAssignedToUser(
        UserEntity trainer, 
        UserEntity user
    );
    
    // Obtener ejercicios con fecha de vencimiento
    List<AssignedExerciseEntity> findByDueDateBefore(LocalDate date);
    
    // Obtener ejercicios vencidos para un usuario
    @Query("SELECT ae FROM AssignedExerciseEntity ae WHERE ae.assignedToUser = :user " +
           "AND ae.dueDate < :currentDate AND ae.status != 'COMPLETED'")
    List<AssignedExerciseEntity> findOverdueExercisesByUser(
        @Param("user") UserEntity user,
        @Param("currentDate") LocalDate currentDate
    );
    
    // Obtener ejercicios asignados en un rango de fechas
    List<AssignedExerciseEntity> findByAssignedDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Obtener ejercicios por prioridad
    List<AssignedExerciseEntity> findByPriorityAndAssignedToUser(
        AssignedExerciseEntity.Priority priority, 
        UserEntity user
    );
    
    // Paginación de ejercicios asignados a un usuario
    Page<AssignedExerciseEntity> findByAssignedToUser(UserEntity user, Pageable pageable);
    
    // Contar ejercicios por estado para un usuario
    Long countByAssignedToUserAndStatus(
        UserEntity user, 
        AssignedExerciseEntity.AssignmentStatus status
    );
    
    // Estadísticas de asignaciones por entrenador
    @Query("SELECT ae.assignedByTrainer, COUNT(ae) FROM AssignedExerciseEntity ae " +
           "WHERE ae.assignedDate BETWEEN :startDate AND :endDate " +
           "GROUP BY ae.assignedByTrainer")
    List<Object[]> getAssignmentStatsByTrainer(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Obtener ejercicios asignados de una biblioteca específica
    List<AssignedExerciseEntity> findByExerciseLibrary(ExerciseLibraryEntity exerciseLibrary);
    
    // Verificar si un ejercicio ya está asignado a un usuario
    boolean existsByExerciseLibraryAndAssignedToUserAndStatusIn(
        ExerciseLibraryEntity exerciseLibrary,
        UserEntity user,
        List<AssignedExerciseEntity.AssignmentStatus> statuses
    );
}
