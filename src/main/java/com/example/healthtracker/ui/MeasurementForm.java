package com.example.healthtracker.ui;

import com.example.healthtracker.model.Measurement;
import com.example.healthtracker.model.Measurement.MeasurementType;
import com.example.healthtracker.model.Person;
import com.example.healthtracker.service.MeasurementService;
import com.example.healthtracker.service.PersonService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDateTime;
import java.util.List;

public class MeasurementForm extends FormLayout {
    
    private final MeasurementService measurementService;
    private final PersonService personService;
    
    private Measurement measurement;
    
    ComboBox<Person> person = new ComboBox<>("Person");
    ComboBox<MeasurementType> type = new ComboBox<>("Measurement Type");
    NumberField measurementValue = new NumberField("Value");
    TextField unit = new TextField("Unit");
    DateTimePicker measuredAt = new DateTimePicker("Measured At");
    TextArea notes = new TextArea("Notes");
    
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button cancel = new Button("Cancel");
    
    Binder<Measurement> binder = new BeanValidationBinder<>(Measurement.class);
    
    public MeasurementForm(MeasurementService measurementService, PersonService personService) {
        this.measurementService = measurementService;
        this.personService = personService;
        
        addClassName("measurement-form");
        
        // Configure form fields
        configureFormFields();
        
        // Configure form fields validation
        binder.bindInstanceFields(this);
        
        add(
            person,
            type,
            measurementValue,
            unit,
            measuredAt,
            notes,
            createButtonsLayout()
        );
    }
    
    private void configureFormFields() {
        // Configure person dropdown
        List<Person> persons = personService.findAllPersons();
        person.setItems(persons);
        person.setItemLabelGenerator(p -> p.getFirstName() + " " + p.getLastName());
        
        // Configure measurement type dropdown
        type.setItems(MeasurementType.values());
        type.setItemLabelGenerator(MeasurementType::name);
        
        // Set current time as default
        measuredAt.setValue(LocalDateTime.now());
        
        // Configure auto-fill of unit based on selected type
        type.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                unit.setValue(e.getValue().getDefaultUnit());
            }
        });
        
        notes.setPlaceholder("Additional information about the measurement");
    }
    
    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        
        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);
        
        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, measurement)));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));
        
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        
        return new HorizontalLayout(save, delete, cancel);
    }
    
    public void setMeasurement(Measurement measurement) {
        this.measurement = measurement;
        binder.readBean(measurement);
    }
    
    private void validateAndSave() {
        try {
            binder.writeBean(measurement);
            fireEvent(new SaveEvent(this, measurement));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
    
    // Events
    public static abstract class MeasurementFormEvent extends ComponentEvent<MeasurementForm> {
        private final Measurement measurement;
        
        protected MeasurementFormEvent(MeasurementForm source, Measurement measurement) {
            super(source, false);
            this.measurement = measurement;
        }
        
        public Measurement getMeasurement() {
            return measurement;
        }
    }
    
    public static class SaveEvent extends MeasurementFormEvent {
        SaveEvent(MeasurementForm source, Measurement measurement) {
            super(source, measurement);
        }
    }
    
    public static class DeleteEvent extends MeasurementFormEvent {
        DeleteEvent(MeasurementForm source, Measurement measurement) {
            super(source, measurement);
        }
    }
    
    public static class CloseEvent extends ComponentEvent<MeasurementForm> {
        CloseEvent(MeasurementForm source) {
            super(source, false);
        }
    }
    
    @Override
    public <T extends ComponentEvent<?>> Registration addListener(
            Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
} 