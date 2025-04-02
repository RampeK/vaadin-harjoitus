package com.example.healthtracker.service;

import com.example.healthtracker.model.MedicalNote;
import com.example.healthtracker.model.MedicalNote.NoteCategory;
import com.example.healthtracker.repository.MedicalNoteRepository;
import com.example.healthtracker.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MedicalNoteServiceImpl implements MedicalNoteService {
    
    private final MedicalNoteRepository medicalNoteRepository;
    private final PersonRepository personRepository;
    
    @Autowired
    public MedicalNoteServiceImpl(MedicalNoteRepository medicalNoteRepository, PersonRepository personRepository) {
        this.medicalNoteRepository = medicalNoteRepository;
        this.personRepository = personRepository;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MedicalNote> findAllNotes() {
        return medicalNoteRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MedicalNote> findNotesByPersonId(Long personId) {
        return medicalNoteRepository.findByPersonId(personId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MedicalNote> findNotesByCategory(NoteCategory category) {
        return medicalNoteRepository.findByCategory(category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MedicalNote> findNotesByPersonIdAndCategory(Long personId, NoteCategory category) {
        return medicalNoteRepository.findByPersonIdAndCategory(personId, category);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MedicalNote> findNotesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return medicalNoteRepository.findByNoteDateBetween(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<MedicalNote> findNotesByDoctorName(String doctorName) {
        return medicalNoteRepository.findByDoctorNameContainingIgnoreCase(doctorName);
    }
    
    @Override
    @Transactional(readOnly = true)
    public MedicalNote findNoteById(Long id) {
        return medicalNoteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Medical note not found with id: " + id));
    }
    
    @Override
    public MedicalNote saveNote(MedicalNote note) {
        if (note.getPerson() != null && note.getPerson().getId() != null) {
            personRepository.findById(note.getPerson().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + note.getPerson().getId()));
        }
        
        return medicalNoteRepository.save(note);
    }
    
    @Override
    public void deleteNote(Long id) {
        if (!medicalNoteRepository.existsById(id)) {
            throw new EntityNotFoundException("Medical note not found with id: " + id);
        }
        medicalNoteRepository.deleteById(id);
    }
} 