package com.sigma.gym.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutineProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    private User user;                 // Usuario que sigue la rutina
    private Routine routine;           // Rutina que está siguiendo
    private int completedDays;         // Cantidad de días completados
    private LocalDate startDate;       // Fecha en que empezó la rutina
    private LocalDate lastUpdate;      // Última vez que se actualizó el progreso
    private boolean isCompleted;       // Si ya terminó la rutina
}