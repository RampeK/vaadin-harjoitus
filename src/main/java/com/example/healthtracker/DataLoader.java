package com.example.healthtracker;

import com.example.healthtracker.model.Measurement;
import com.example.healthtracker.model.Measurement.MeasurementType;
import com.example.healthtracker.model.Person;
import com.example.healthtracker.model.Person.Gender;
import com.example.healthtracker.repository.MeasurementRepository;
import com.example.healthtracker.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {

    private final PersonRepository personRepository;
    private final MeasurementRepository measurementRepository;
    private final Random random = new Random();

    @Autowired
    public DataLoader(PersonRepository personRepository, MeasurementRepository measurementRepository) {
        this.personRepository = personRepository;
        this.measurementRepository = measurementRepository;
    }

    @Override
    public void run(String... args) {
        if (personRepository.count() == 0) {
            loadTestData();
        }
    }

    private void loadTestData() {
        // Create test persons
        List<Person> persons = Arrays.asList(
            createPerson("John", "Doe", LocalDate.of(1980, Month.JANUARY, 15), Gender.MALE, "john.doe@example.com", "040-1234567"),
            createPerson("Jane", "Smith", LocalDate.of(1985, Month.MARCH, 22), Gender.FEMALE, "jane.smith@example.com", "050-7654321"),
            createPerson("Mike", "Johnson", LocalDate.of(1975, Month.JULY, 10), Gender.MALE, "mike.j@example.com", "045-3456789"),
            createPerson("Sarah", "Williams", LocalDate.of(1990, Month.NOVEMBER, 5), Gender.FEMALE, "sarahw@example.com", "040-9876543"),
            createPerson("Robert", "Brown", LocalDate.of(1965, Month.MAY, 28), Gender.MALE, "rbrown@example.com", "050-1122334")
        );
        
        personRepository.saveAll(persons);
        
        // Create measurements for each person
        persons.forEach(this::createMeasurementsForPerson);
    }
    
    private Person createPerson(String firstName, String lastName, LocalDate birthDate, Gender gender, String email, String phoneNumber) {
        Person person = new Person(firstName, lastName, birthDate, gender);
        person.setEmail(email);
        person.setPhoneNumber(phoneNumber);
        return person;
    }
    
    private void createMeasurementsForPerson(Person person) {
        // Create blood pressure measurements
        for (int i = 0; i < 5; i++) {
            LocalDateTime measurementTime = LocalDateTime.now().minusDays(i * 7);
            Measurement bloodPressure = new Measurement(person, MeasurementType.BLOOD_PRESSURE, 120.0 + random.nextDouble() * 20.0);
            bloodPressure.setMeasuredAt(measurementTime);
            bloodPressure.setNotes("Regular checkup");
            measurementRepository.save(bloodPressure);
        }
        
        // Create heart rate measurements
        for (int i = 0; i < 5; i++) {
            LocalDateTime measurementTime = LocalDateTime.now().minusDays(i * 7);
            Measurement heartRate = new Measurement(person, MeasurementType.HEART_RATE, 70.0 + random.nextDouble() * 15.0);
            heartRate.setMeasuredAt(measurementTime);
            measurementRepository.save(heartRate);
        }
        
        // Create weight measurements
        for (int i = 0; i < 3; i++) {
            LocalDateTime measurementTime = LocalDateTime.now().minusMonths(i);
            Measurement weight = new Measurement(person, MeasurementType.WEIGHT, 70.0 + random.nextDouble() * 15.0);
            weight.setMeasuredAt(measurementTime);
            measurementRepository.save(weight);
        }
        
        // Create height measurement
        Measurement height = new Measurement(person, MeasurementType.HEIGHT, 165.0 + random.nextDouble() * 25.0);
        height.setMeasuredAt(LocalDateTime.now().minusMonths(6));
        measurementRepository.save(height);
        
        // Create temperature measurements
        for (int i = 0; i < 2; i++) {
            LocalDateTime measurementTime = LocalDateTime.now().minusDays(i * 2);
            Measurement temperature = new Measurement(person, MeasurementType.TEMPERATURE, 36.5 + random.nextDouble() * 1.5);
            temperature.setMeasuredAt(measurementTime);
            measurementRepository.save(temperature);
        }
    }
} 