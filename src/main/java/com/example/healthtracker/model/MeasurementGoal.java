package com.example.healthtracker.model;

import com.example.healthtracker.model.Measurement.MeasurementType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Entity
@Table(name = "measurement_goals")
public class MeasurementGoal {
    
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
    @Column(name = "target_value")
    private Double targetValue;
    
    private String unit;
    
    @NotNull
    private LocalDate targetDate;
    
    private boolean achieved;
    
    private String notes;
    
    public MeasurementGoal() {
        this.targetDate = LocalDate.now().plusMonths(1);
        this.achieved = false;
    }
    
    public MeasurementGoal(Person person, MeasurementType type, Double targetValue, LocalDate targetDate) {
        this.person = person;
        this.type = type;
        this.targetValue = targetValue;
        this.unit = type.getDefaultUnit();
        this.targetDate = targetDate;
        this.achieved = false;
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
    
    public Double getTargetValue() {
        return targetValue;
    }
    
    public void setTargetValue(Double targetValue) {
        this.targetValue = targetValue;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public LocalDate getTargetDate() {
        return targetDate;
    }
    
    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
    
    public boolean isAchieved() {
        return achieved;
    }
    
    public void setAchieved(boolean achieved) {
        this.achieved = achieved;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
} 