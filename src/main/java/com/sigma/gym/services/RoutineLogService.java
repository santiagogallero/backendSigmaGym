package com.sigma.gym.services;

import com.sigma.gym.model.RoutineLog;
import java.util.List;

public interface RoutineLogService {
    RoutineLog createRoutineLog(RoutineLog log);
    RoutineLog getRoutineLogById(Long id);
    List<RoutineLog> getAllRoutineLogs();
    RoutineLog updateRoutineLog(Long id, RoutineLog log);
    void deleteRoutineLog(Long id);
}
