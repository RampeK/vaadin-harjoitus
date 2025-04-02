package com.example.healthtracker.service;

import com.example.healthtracker.model.MedicalNote;
import com.example.healthtracker.model.MedicalNote.NoteCategory;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicalNoteService {
    
    List<MedicalNote> findAllNotes();
    
    List<MedicalNote> findNotesByPersonId(Long personId);
    
    List<MedicalNote> findNotesByCategory(NoteCategory category);
    
    List<MedicalNote> findNotesByPersonIdAndCategory(Long personId, NoteCategory category);
    
    List<MedicalNote> findNotesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<MedicalNote> findNotesByDoctorName(String doctorName);
    
    MedicalNote findNoteById(Long id);
    
    MedicalNote saveNote(MedicalNote note);
    
    void deleteNote(Long id);
} 