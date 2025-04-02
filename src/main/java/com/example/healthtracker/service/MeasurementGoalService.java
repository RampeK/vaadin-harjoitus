package com.example.healthtracker.service;

import com.example.healthtracker.model.MeasurementGoal;
import com.example.healthtracker.model.Measurement.MeasurementType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MeasurementGoalService {
    
    List<MeasurementGoal> findAllGoals();
    
    Optional<MeasurementGoal> findGoalById(Long id);
    
    List<MeasurementGoal> findGoalsByPersonId(Long personId);
    
    List<MeasurementGoal> findGoalsByPersonIdAndType(Long personId, MeasurementType type);
    
    List<MeasurementGoal> findUpcomingGoalsByPersonId(Long personId, LocalDate date);
    
    List<MeasurementGoal> findGoalsByPersonIdAndAchievementStatus(Long personId, boolean achieved);
    
    MeasurementGoal saveGoal(MeasurementGoal goal);
    
    void deleteGoal(Long id);
    
    void checkAndUpdateGoalStatus(Long personId);
} 