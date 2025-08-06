package com.sigma.gym.services;

import com.sigma.gym.model.Attendance;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    Attendance create(Attendance attendance);
    Attendance getById(Long id);
    List<Attendance> getAll();
    Attendance update(Long id, Attendance attendance);
    void delete(Long id);
    List<Attendance> getByUserId(Long userId);
    List<Attendance> getByDate(LocalDate date);
}
