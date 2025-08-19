package com.sigma.gym.repository;

import com.sigma.gym.entity.EnhancedRoutineProgressEntity;
import com.sigma.gym.entity.UserEntity;
import com.sigma.gym.entity.RoutineEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnhancedRoutineProgressRepository extends JpaRepository<EnhancedRoutineProgressEntity, Long> {
    
    // Obtener progreso por usuario
    List<EnhancedRoutineProgressEntity> findByUser(UserEntity user);
    
    // Obtener progreso por rutina
    List<EnhancedRoutineProgressEntity> findByRoutine(RoutineEntity routine);
    
    // Obtener progreso específico de un usuario en una rutina
    Optional<EnhancedRoutineProgressEntity> findByUserAndRoutine(UserEntity user, RoutineEntity routine);
    
    // Obtener progreso por estado
    List<EnhancedRoutineProgressEntity> findByStatus(EnhancedRoutineProgressEntity.ProgressStatus status);
    
    // Obtener progreso activo de un usuario
    List<EnhancedRoutineProgressEntity> findByUserAndStatus(
        UserEntity user, 
        EnhancedRoutineProgressEntity.ProgressStatus status
    );
    
    // Obtener rutinas completadas por usuario
    List<EnhancedRoutineProgressEntity> findByUserAndStatusOrderByActualEndDateDesc(
        UserEntity user, 
        EnhancedRoutineProgressEntity.ProgressStatus status
    );
    
    // Obtener rutinas con progreso en un rango de fechas
    List<EnhancedRoutineProgressEntity> findByStartDateBetween(LocalDate startDate, LocalDate endDate);
    
    // Obtener rutinas vencidas
    @Query("SELECT erp FROM EnhancedRoutineProgressEntity erp WHERE erp.targetEndDate < :currentDate " +
           "AND erp.status NOT IN ('COMPLETED', 'CANCELLED')")
    List<EnhancedRoutineProgressEntity> findOverdueRoutines(@Param("currentDate") LocalDate currentDate);
    
    // Obtener estadísticas de progreso por usuario
    @Query("SELECT erp.user, AVG(erp.completionPercentage), COUNT(erp) FROM EnhancedRoutineProgressEntity erp " +
           "WHERE erp.startDate BETWEEN :startDate AND :endDate " +
           "GROUP BY erp.user")
    List<Object[]> getProgressStatsByUser(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
    
    // Obtener progreso con mayor porcentaje de completitud
    @Query("SELECT erp FROM EnhancedRoutineProgressEntity erp WHERE erp.user = :user " +
           "ORDER BY erp.completionPercentage DESC")
    List<EnhancedRoutineProgressEntity> findByUserOrderByCompletionPercentageDesc(
        @Param("user") UserEntity user,
        Pageable pageable
    );
    
    // Contar rutinas por estado para un usuario
    Long countByUserAndStatus(UserEntity user, EnhancedRoutineProgressEntity.ProgressStatus status);
    
    // Obtener progreso reciente por usuario
    @Query("SELECT erp FROM EnhancedRoutineProgressEntity erp WHERE erp.user = :user " +
           "ORDER BY erp.lastSessionDate DESC NULLS LAST")
    List<EnhancedRoutineProgressEntity> findRecentProgressByUser(
        @Param("user") UserEntity user,
        Pageable pageable
    );
    
    // Obtener rutinas con próxima sesión programada
    List<EnhancedRoutineProgressEntity> findByNextScheduledSessionBetween(
        java.time.LocalDateTime start,
        java.time.LocalDateTime end
    );
    
    // Paginación de progreso por usuario
    Page<EnhancedRoutineProgressEntity> findByUser(UserEntity user, Pageable pageable);
    
    // Obtener promedio de satisfacción por usuario
    @Query("SELECT AVG(erp.satisfactionRating) FROM EnhancedRoutineProgressEntity erp " +
           "WHERE erp.user = :user AND erp.satisfactionRating IS NOT NULL")
    Double getAverageSatisfactionByUser(@Param("user") UserEntity user);
}
