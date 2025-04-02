package com.example.healthtracker.repository;

import com.example.healthtracker.model.MedicalNote;
import com.example.healthtracker.model.MedicalNote.NoteCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MedicalNoteRepository extends JpaRepository<MedicalNote, Long> {

    List<MedicalNote> findByPersonId(Long personId);

    List<MedicalNote> findByCategory(NoteCategory category);

    List<MedicalNote> findByPersonIdAndCategory(Long personId, NoteCategory category);

    List<MedicalNote> findByNoteDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    List<MedicalNote> findByDoctorNameContainingIgnoreCase(String doctorName);
} 