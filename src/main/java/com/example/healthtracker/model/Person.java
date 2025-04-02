package com.example.healthtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "persons")
public class Person {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;
    
    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    private String email;
    
    private String phoneNumber;
    
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Measurement> measurements = new ArrayList<>();
    
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MeasurementGoal> goals = new ArrayList<>();
    
    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<MedicalNote> medicalNotes = new ArrayList<>();
    
    public enum Gender {
        MALE, FEMALE, OTHER
    }

    public Person() {
    }

    public Person(String firstName, String lastName, LocalDate birthDate, Gender gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public Gender getGender() {
        return gender;
    }
    
    public void setGender(Gender gender) {
        this.gender = gender;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public List<Measurement> getMeasurements() {
        return measurements;
    }
    
    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }
    
    public List<MeasurementGoal> getGoals() {
        return goals;
    }
    
    public void setGoals(List<MeasurementGoal> goals) {
        this.goals = goals;
    }
    
    public List<MedicalNote> getMedicalNotes() {
        return medicalNotes;
    }
    
    public void setMedicalNotes(List<MedicalNote> medicalNotes) {
        this.medicalNotes = medicalNotes;
    }
    
    public void addMeasurement(Measurement measurement) {
        measurements.add(measurement);
        measurement.setPerson(this);
    }

    public void removeMeasurement(Measurement measurement) {
        measurements.remove(measurement);
        measurement.setPerson(null);
    }

    public void addGoal(MeasurementGoal goal) {
        goals.add(goal);
        goal.setPerson(this);
    }

    public void removeGoal(MeasurementGoal goal) {
        goals.remove(goal);
        goal.setPerson(null);
    }

    public void addMedicalNote(MedicalNote medicalNote) {
        medicalNotes.add(medicalNote);
        medicalNote.setPerson(this);
    }

    public void removeMedicalNote(MedicalNote medicalNote) {
        medicalNotes.remove(medicalNote);
        medicalNote.setPerson(null);
    }
} 