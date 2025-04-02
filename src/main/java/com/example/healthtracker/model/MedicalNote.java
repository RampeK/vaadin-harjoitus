package com.example.healthtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
public class MedicalNote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "person_id")
    @NotNull(message = "Person is required")
    private Person person;
    
    @NotNull(message = "Date is required")
    private LocalDateTime noteDate = LocalDateTime.now();
    
    @NotEmpty(message = "Title cannot be empty")
    private String title;
    
    @Column(length = 2000)
    private String content;
    
    private String doctorName;
    
    @Enumerated(EnumType.STRING)
    private NoteCategory category = NoteCategory.GENERAL;
    
    public enum NoteCategory {
        GENERAL("General"),
        DIAGNOSIS("Diagnosis"),
        TREATMENT("Treatment"),
        MEDICATION("Medication"),
        FOLLOWUP("Follow-up");
        
        private final String displayName;
        
        NoteCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public MedicalNote() {
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
    
    public LocalDateTime getNoteDate() {
        return noteDate;
    }
    
    public void setNoteDate(LocalDateTime noteDate) {
        this.noteDate = noteDate;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }
    
    public NoteCategory getCategory() {
        return category;
    }
    
    public void setCategory(NoteCategory category) {
        this.category = category;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MedicalNote that = (MedicalNote) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
} 