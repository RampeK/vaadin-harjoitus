package com.example.healthtracker.service;

import com.example.healthtracker.model.Measurement;
import com.example.healthtracker.model.MeasurementGoal;
import com.example.healthtracker.model.Measurement.MeasurementType;
import com.example.healthtracker.repository.MeasurementGoalRepository;
import com.example.healthtracker.repository.MeasurementRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MeasurementGoalServiceImpl implements MeasurementGoalService {

    private final MeasurementGoalRepository goalRepository;
    private final MeasurementRepository measurementRepository;

    @Autowired
    public MeasurementGoalServiceImpl(MeasurementGoalRepository goalRepository, MeasurementRepository measurementRepository) {
        this.goalRepository = goalRepository;
        this.measurementRepository = measurementRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeasurementGoal> findAllGoals() {
        return goalRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MeasurementGoal> findGoalById(Long id) {
        return goalRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeasurementGoal> findGoalsByPersonId(Long personId) {
        return goalRepository.findByPersonId(personId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeasurementGoal> findGoalsByPersonIdAndType(Long personId, MeasurementType type) {
        return goalRepository.findByPersonIdAndType(personId, type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeasurementGoal> findUpcomingGoalsByPersonId(Long personId, LocalDate date) {
        return goalRepository.findByPersonIdAndTargetDateBefore(personId, date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MeasurementGoal> findGoalsByPersonIdAndAchievementStatus(Long personId, boolean achieved) {
        return goalRepository.findByPersonIdAndAchieved(personId, achieved);
    }

    @Override
    @Transactional
    public MeasurementGoal saveGoal(MeasurementGoal goal) {
        return goalRepository.save(goal);
    }

    @Override
    @Transactional
    public void deleteGoal(Long id) {
        goalRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found with id: " + id));
        goalRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void checkAndUpdateGoalStatus(Long personId) {
        List<MeasurementGoal> goals = goalRepository.findByPersonIdAndAchieved(personId, false);
        for (MeasurementGoal goal : goals) {
            List<Measurement> recentMeasurements = measurementRepository.findByPersonIdAndType(personId, goal.getType());
            
            if (!recentMeasurements.isEmpty()) {
                recentMeasurements.sort((m1, m2) -> m2.getMeasuredAt().compareTo(m1.getMeasuredAt()));
                Measurement latestMeasurement = recentMeasurements.get(0);
                
                if (goal.getType() == MeasurementType.WEIGHT || goal.getType() == MeasurementType.BLOOD_PRESSURE) {
                    if (latestMeasurement.getMeasurementValue() <= goal.getTargetValue()) {
                        goal.setAchieved(true);
                        goalRepository.save(goal);
                    }
                } else {
                    if (latestMeasurement.getMeasurementValue() >= goal.getTargetValue()) {
                        goal.setAchieved(true);
                        goalRepository.save(goal);
                    }
                }
            }
        }
    }
} 