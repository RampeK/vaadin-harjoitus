package com.example.healthtracker.controller;

import com.example.healthtracker.model.Measurement;
import com.example.healthtracker.model.Measurement.MeasurementType;
import com.example.healthtracker.service.MeasurementService;
import com.example.healthtracker.service.PersonService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

    private final MeasurementService measurementService;
    private final PersonService personService;

    @Autowired
    public MeasurementController(MeasurementService measurementService, PersonService personService) {
        this.measurementService = measurementService;
        this.personService = personService;
    }

    @GetMapping
    public ResponseEntity<List<Measurement>> getAllMeasurements() {
        return ResponseEntity.ok(measurementService.findAllMeasurements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Measurement> getMeasurementById(@PathVariable Long id) {
        return measurementService.findMeasurementById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Measurement not found with id: " + id));
    }

    @GetMapping("/person/{personId}")
    public ResponseEntity<List<Measurement>> getMeasurementsByPersonId(@PathVariable Long personId) {
        if (!personService.findPersonById(personId).isPresent()) {
            throw new EntityNotFoundException("Person not found with id: " + personId);
        }
        return ResponseEntity.ok(measurementService.findMeasurementsByPersonId(personId));
    }

    @GetMapping("/person/{personId}/type/{type}")
    public ResponseEntity<List<Measurement>> getMeasurementsByPersonIdAndType(
            @PathVariable Long personId,
            @PathVariable MeasurementType type) {
        if (!personService.findPersonById(personId).isPresent()) {
            throw new EntityNotFoundException("Person not found with id: " + personId);
        }
        return ResponseEntity.ok(measurementService.findMeasurementsByPersonIdAndType(personId, type));
    }

    @GetMapping("/person/{personId}/date-range")
    public ResponseEntity<List<Measurement>> getMeasurementsByPersonIdAndDateRange(
            @PathVariable Long personId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (!personService.findPersonById(personId).isPresent()) {
            throw new EntityNotFoundException("Person not found with id: " + personId);
        }
        return ResponseEntity.ok(measurementService.findMeasurementsByPersonIdAndDateRange(personId, start, end));
    }

    @GetMapping("/person/{personId}/type/{type}/date-range")
    public ResponseEntity<List<Measurement>> getMeasurementsByPersonIdAndTypeAndDateRange(
            @PathVariable Long personId,
            @PathVariable MeasurementType type,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        if (!personService.findPersonById(personId).isPresent()) {
            throw new EntityNotFoundException("Person not found with id: " + personId);
        }
        return ResponseEntity.ok(measurementService.findMeasurementsByPersonIdAndTypeAndDateRange(personId, type, start, end));
    }

    @PostMapping
    public ResponseEntity<Measurement> createMeasurement(@Valid @RequestBody Measurement measurement) {
        if (measurement.getPerson() == null || measurement.getPerson().getId() == null) {
            throw new IllegalArgumentException("Person is required for a measurement");
        }
        personService.findPersonById(measurement.getPerson().getId())
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + measurement.getPerson().getId()));
        return new ResponseEntity<>(measurementService.saveMeasurement(measurement), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Measurement> updateMeasurement(@PathVariable Long id, @Valid @RequestBody Measurement measurement) {
        measurementService.findMeasurementById(id)
                .orElseThrow(() -> new EntityNotFoundException("Measurement not found with id: " + id));
        measurement.setId(id);
        if (measurement.getPerson() == null || measurement.getPerson().getId() == null) {
            throw new IllegalArgumentException("Person is required for a measurement");
        }
        personService.findPersonById(measurement.getPerson().getId())
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + measurement.getPerson().getId()));
        return ResponseEntity.ok(measurementService.saveMeasurement(measurement));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMeasurement(@PathVariable Long id) {
        measurementService.deleteMeasurement(id);
        return ResponseEntity.noContent().build();
    }
} 