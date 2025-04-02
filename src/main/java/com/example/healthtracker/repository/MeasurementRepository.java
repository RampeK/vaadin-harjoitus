package com.example.healthtracker.repository;

import com.example.healthtracker.model.Measurement;
import com.example.healthtracker.model.Measurement.MeasurementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    
    List<Measurement> findByPersonId(Long personId);
    
    List<Measurement> findByPersonIdAndType(Long personId, MeasurementType type);
    
    List<Measurement> findByPersonIdAndMeasuredAtBetween(Long personId, LocalDateTime start, LocalDateTime end);
    
    List<Measurement> findByPersonIdAndTypeAndMeasuredAtBetween(Long personId, MeasurementType type, LocalDateTime start, LocalDateTime end);
} 