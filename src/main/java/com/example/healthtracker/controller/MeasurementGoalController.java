package com.example.healthtracker.controller;

import com.example.healthtracker.model.MeasurementGoal;
import com.example.healthtracker.model.Measurement.MeasurementType;
import com.example.healthtracker.service.MeasurementGoalService;
import com.example.healthtracker.service.PersonService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class MeasurementGoalController {

    private final MeasurementGoalService goalService;
    private final PersonService personService;

    @Autowired
    public MeasurementGoalController(MeasurementGoalService goalService, PersonService personService) {
        this.goalService = goalService;
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<MeasurementGoal>> getAllGoals() {
        return ResponseEntity.ok(goalService.findAllGoals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeasurementGoal> getGoalById(@PathVariable Long id) {
        return goalService.findGoalById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found with id: " + id));
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<List<MeasurementGoal>> getGoalsByPersonId(@PathVariable Long personId) {
        if (!personService.findPersonById(personId).isPresent()) {
            throw new EntityNotFoundException("Person not found with id: " + personId);
        }
        return ResponseEntity.ok(goalService.findGoalsByPersonId(personId));
    }

    @GetMapping("/person/{personId}/type/{type}")
    public ResponseEntity<List<MeasurementGoal>> getGoalsByPersonIdAndType(
            @PathVariable Long personId,
            @PathVariable MeasurementType type) {
        if (!personService.findPersonById(personId).isPresent()) {
            throw new EntityNotFoundException("Person not found with id: " + personId);
        }
        return ResponseEntity.ok(goalService.findGoalsByPersonIdAndType(personId, type));
    }

    @GetMapping("/person/{personId}/upcoming")
    public ResponseEntity<List<MeasurementGoal>> getUpcomingGoalsByPersonId(
            @PathVariable Long personId,
            @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (!personService.findPersonById(personId).isPresent()) {
            throw new EntityNotFoundException("Person not found with id: " + personId);
        }
        return ResponseEntity.ok(goalService.findUpcomingGoalsByPersonId(personId, date));
    }

    @GetMapping("/person/{personId}/achieved/{achieved}")
    public ResponseEntity<List<MeasurementGoal>> getGoalsByPersonIdAndAchievementStatus(
            @PathVariable Long personId,
            @PathVariable boolean achieved) {
        if (!personService.findPersonById(personId).isPresent()) {
            throw new EntityNotFoundException("Person not found with id: " + personId);
        }
        return ResponseEntity.ok(goalService.findGoalsByPersonIdAndAchievementStatus(personId, achieved));
    }

    @PostMapping
    public ResponseEntity<MeasurementGoal> createGoal(@Valid @RequestBody MeasurementGoal goal) {
        if (goal.getPerson() == null || goal.getPerson().getId() == null) {
            throw new IllegalArgumentException("Person is required for a goal");
        }
        personService.findPersonById(goal.getPerson().getId())
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + goal.getPerson().getId()));
        return new ResponseEntity<>(goalService.saveGoal(goal), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MeasurementGoal> updateGoal(@PathVariable Long id, @Valid @RequestBody MeasurementGoal goal) {
        goalService.findGoalById(id)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found with id: " + id));
        goal.setId(id);
        if (goal.getPerson() == null || goal.getPerson().getId() == null) {
            throw new IllegalArgumentException("Person is required for a goal");
        }
        personService.findPersonById(goal.getPerson().getId())
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + goal.getPerson().getId()));
        return ResponseEntity.ok(goalService.saveGoal(goal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/person/{personId}/check-status")
    public ResponseEntity<Void> checkAndUpdateGoalStatus(@PathVariable Long personId) {
        if (!personService.findPersonById(personId).isPresent()) {
            throw new EntityNotFoundException("Person not found with id: " + personId);
        }
        goalService.checkAndUpdateGoalStatus(personId);
        return ResponseEntity.ok().build();
    }
} 