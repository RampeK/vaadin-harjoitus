package com.example.healthtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Entity
@Table(name = "measurements")
public class Measurement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "person_id")
    private Person person;
    
    @NotNull
    @Enumerated(EnumType.STRING)
    private MeasurementType type;
    
    @NotNull
    @PositiveOrZero
    @Column(name = "measurement_value")
    private Double measurementValue;
    
    private String unit;
    
    @PastOrPresent
    private LocalDateTime measuredAt;
    
    private String notes;
    
    public enum MeasurementType {
        BLOOD_PRESSURE("mmHg"),
        HEART_RATE("bpm"),
        WEIGHT("kg"),
        HEIGHT("cm"),
        TEMPERATURE("°C"),
        BLOOD_SUGAR("mmol/L");
        
        private final String defaultUnit;
        
        MeasurementType(String defaultUnit) {
            this.defaultUnit = defaultUnit;
        }
        
        public String getDefaultUnit() {
            return defaultUnit;
        }
    }
    
    public Measurement() {
        this.measuredAt = LocalDateTime.now();
    }
    

    public Measurement(Person person, MeasurementType type, Double measurementValue) {
        this.person = person;
        this.type = type;
        this.measurementValue = measurementValue;
        this.unit = type.getDefaultUnit();
        this.measuredAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Person getPerson() {
        return person;
    }
    
    public void setPerson(Person person) {
        this.person = person;
    }
    
    public MeasurementType getType() {
        return type;
    }
    
    public void setType(MeasurementType type) {
        this.type = type;
        if (this.unit == null) {
            this.unit = type.getDefaultUnit();
        }
    }
    
    public Double getMeasurementValue() {
        return measurementValue;
    }
    
    public void setMeasurementValue(Double measurementValue) {
        this.measurementValue = measurementValue;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }
    
    public void setMeasuredAt(LocalDateTime measuredAt) {
        this.measuredAt = measuredAt;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
} 