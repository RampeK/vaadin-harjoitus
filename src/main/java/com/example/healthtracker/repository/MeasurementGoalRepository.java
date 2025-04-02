package com.example.healthtracker.repository;

import com.example.healthtracker.model.MeasurementGoal;
import com.example.healthtracker.model.Measurement.MeasurementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MeasurementGoalRepository extends JpaRepository<MeasurementGoal, Long> {
    
    List<MeasurementGoal> findByPersonId(Long personId);
    
    List<MeasurementGoal> findByPersonIdAndType(Long personId, MeasurementType type);
    
    List<MeasurementGoal> findByPersonIdAndTargetDateBefore(Long personId, LocalDate date);
    
    List<MeasurementGoal> findByPersonIdAndAchieved(Long personId, boolean achieved);
} 