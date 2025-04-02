package com.example.healthtracker.service;

import com.example.healthtracker.model.Measurement;
import com.example.healthtracker.model.Measurement.MeasurementType;
import com.example.healthtracker.repository.MeasurementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MeasurementServiceImpl implements MeasurementService {

    private final MeasurementRepository measurementRepository;

    @Autowired
    public MeasurementServiceImpl(MeasurementRepository measurementRepository) {
        this.measurementRepository = measurementRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Measurement> findAllMeasurements() {
        return measurementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Measurement> findMeasurementById(Long id) {
        return measurementRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Measurement> findMeasurementsByPersonId(Long personId) {
        return measurementRepository.findByPersonId(personId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Measurement> findMeasurementsByPersonIdAndType(Long personId, MeasurementType type) {
        return measurementRepository.findByPersonIdAndType(personId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Measurement> findMeasurementsByPersonIdAndDateRange(Long personId, LocalDateTime start, LocalDateTime end) {
        return measurementRepository.findByPersonIdAndMeasuredAtBetween(personId, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Measurement> findMeasurementsByPersonIdAndTypeAndDateRange(Long personId, MeasurementType type, LocalDateTime start, LocalDateTime end) {
        return measurementRepository.findByPersonIdAndTypeAndMeasuredAtBetween(personId, type, start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Measurement> findRecentMeasurements(int limit) {
        return measurementRepository.findAll().stream()
                .sorted((m1, m2) -> m2.getMeasuredAt().compareTo(m1.getMeasuredAt()))
                .limit(limit)
                .toList();
    }

    @Override
    @Transactional
    public Measurement saveMeasurement(Measurement measurement) {
        return measurementRepository.save(measurement);
    }

    @Override
    @Transactional
    public void deleteMeasurement(Long id) {
        measurementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Measurement not found with id: " + id));
        measurementRepository.deleteById(id);
    }
} 