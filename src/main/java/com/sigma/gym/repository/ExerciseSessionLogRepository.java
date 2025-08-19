package com.sigma.gym.repository;

import com.sigma.gym.entity.ExerciseSessionLogEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.entity.AssignedExerciseEntity;
import com.sigma.gym.entity.EnhancedRoutineProgressEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExerciseSessionLogRepository extends JpaRepository<ExerciseSessionLogEntity, Long> {
    
    // Obtener sesiones por usuario
    List<ExerciseSessionLogEntity> findByUser(UserEntity user);
    
    // Obtener sesiones por usuario ordenadas por fecha
    List<ExerciseSessionLogEntity> findByUserOrderBySessionDateDesc(UserEntity user);
    
    // Obtener sesiones por ejercicio asignado
    List<ExerciseSessionLogEntity> findByAssignedExercise(AssignedExerciseEntity assignedExercise);
    
    // Obtener sesiones por progreso de rutina
    List<ExerciseSessionLogEntity> findByRoutineProgress(EnhancedRoutineProgressEntity routineProgress);
    
    // Obtener sesiones por estado
    List<ExerciseSessionLogEntity> findByStatus(ExerciseSessionLogEntity.SessionStatus status);
    
    // Obtener sesiones en un rango de fechas
    List<ExerciseSessionLogEntity> findBySessionDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Obtener sesiones de un usuario en un rango de fechas
    List<ExerciseSessionLogEntity> findByUserAndSessionDateBetween(
        UserEntity user,
        LocalDate startDate,
        LocalDate endDate
    );
    
    // Obtener sesiones completadas de un usuario
    List<ExerciseSessionLogEntity> findByUserAndStatus(
        UserEntity user,
        ExerciseSessionLogEntity.SessionStatus status
    );
    
    // Paginación de sesiones por usuario
    Page<ExerciseSessionLogEntity> findByUserOrderBySessionDateDesc(UserEntity user, Pageable pageable);
    
    // Contar sesiones por usuario y estado
    Long countByUserAndStatus(UserEntity user, ExerciseSessionLogEntity.SessionStatus status);
    
    // Estadísticas de tiempo total por usuario
    @Query("SELECT SUM(esl.durationMinutes) FROM ExerciseSessionLogEntity esl " +
           "WHERE esl.user = :user AND esl.sessionDate BETWEEN :startDate AND :endDate " +
           "AND esl.status = 'COMPLETED'")
    Long getTotalExerciseTimeByUser(
        @Param("user") UserEntity user,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Obtener promedio de calorías quemadas por usuario
    @Query("SELECT AVG(esl.caloriesBurned) FROM ExerciseSessionLogEntity esl " +
           "WHERE esl.user = :user AND esl.caloriesBurned IS NOT NULL " +
           "AND esl.sessionDate BETWEEN :startDate AND :endDate")
    Double getAverageCaloriesByUser(
        @Param("user") UserEntity user,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Obtener promedio de RPE por usuario
    @Query("SELECT AVG(esl.rpeRating) FROM ExerciseSessionLogEntity esl " +
           "WHERE esl.user = :user AND esl.rpeRating IS NOT NULL " +
           "AND esl.sessionDate BETWEEN :startDate AND :endDate")
    Double getAverageRPEByUser(
        @Param("user") UserEntity user,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Obtener sesiones más recientes por usuario
    @Query("SELECT esl FROM ExerciseSessionLogEntity esl " +
           "WHERE esl.user = :user ORDER BY esl.sessionDate DESC, esl.startTime DESC")
    List<ExerciseSessionLogEntity> findRecentSessionsByUser(
        @Param("user") UserEntity user,
        Pageable pageable
    );
    
    // Obtener resumen diario de actividad
    @Query("SELECT esl.sessionDate, COUNT(esl), SUM(esl.durationMinutes), AVG(esl.rpeRating) " +
           "FROM ExerciseSessionLogEntity esl " +
           "WHERE esl.user = :user AND esl.sessionDate BETWEEN :startDate AND :endDate " +
           "AND esl.status = 'COMPLETED' " +
           "GROUP BY esl.sessionDate ORDER BY esl.sessionDate DESC")
    List<Object[]> getDailyActivitySummary(
        @Param("user") UserEntity user,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Verificar si hay sesiones para una fecha específica
    boolean existsByUserAndSessionDate(UserEntity user, LocalDate sessionDate);
    
    // Obtener la última sesión de un usuario
    @Query("SELECT esl FROM ExerciseSessionLogEntity esl " +
           "WHERE esl.user = :user ORDER BY esl.sessionDate DESC, esl.endTime DESC")
    List<ExerciseSessionLogEntity> findLastSessionByUser(@Param("user") UserEntity user, Pageable pageable);
}
