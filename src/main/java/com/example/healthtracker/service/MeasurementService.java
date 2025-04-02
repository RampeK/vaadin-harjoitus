package com.example.healthtracker.service;

import com.example.healthtracker.model.Measurement;
import com.example.healthtracker.model.Measurement.MeasurementType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeasurementService {
    
    List<Measurement> findAllMeasurements();
    
    Optional<Measurement> findMeasurementById(Long id);
    
    List<Measurement> findMeasurementsByPersonId(Long personId);
    
    List<Measurement> findMeasurementsByPersonIdAndType(Long personId, MeasurementType type);
    
    List<Measurement> findMeasurementsByPersonIdAndDateRange(Long personId, LocalDateTime start, LocalDateTime end);
    
    List<Measurement> findMeasurementsByPersonIdAndTypeAndDateRange(Long personId, MeasurementType type, LocalDateTime start, LocalDateTime end);
    
    /**
     * Find the most recent measurements with person data eagerly loaded.
     * @param limit Maximum number of measurements to return
     * @return List of measurements sorted by measured date in descending order
     */
    List<Measurement> findRecentMeasurements(int limit);
    
    Measurement saveMeasurement(Measurement measurement);
    
    void deleteMeasurement(Long id);
} 