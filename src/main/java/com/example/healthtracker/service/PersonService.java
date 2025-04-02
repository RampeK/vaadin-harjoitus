package com.example.healthtracker.service;

import com.example.healthtracker.model.Person;

import java.util.List;
import java.util.Optional;

public interface PersonService {
    
    List<Person> findAllPersons();
    
    Optional<Person> findPersonById(Long id);
    
    List<Person> findPersonsByName(String searchTerm);
    
    Person savePerson(Person person);
    
    void deletePerson(Long id);
} 