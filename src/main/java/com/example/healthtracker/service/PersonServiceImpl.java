package com.example.healthtracker.service;

import com.example.healthtracker.model.Person;
import com.example.healthtracker.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PersonServiceImpl implements PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> findAllPersons() {
        return personRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Person> findPersonById(Long id) {
        return personRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Person> findPersonsByName(String searchTerm) {
        return personRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(searchTerm, searchTerm);
    }

    @Override
    @Transactional
    public Person savePerson(Person person) {
        return personRepository.save(person);
    }

    @Override
    @Transactional
    public void deletePerson(Long id) {
        personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));
        personRepository.deleteById(id);
    }
} 