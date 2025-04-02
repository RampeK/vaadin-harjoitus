package com.example.healthtracker.ui;

import com.example.healthtracker.model.MeasurementGoal;
import com.example.healthtracker.model.Measurement.MeasurementType;
import com.example.healthtracker.model.Person;
import com.example.healthtracker.service.MeasurementGoalService;
import com.example.healthtracker.service.PersonService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDate;
import java.util.List;

public class MeasurementGoalForm extends FormLayout {
    
    private final MeasurementGoalService goalService;
    private final PersonService personService;
    
    private MeasurementGoal goal;
    
    ComboBox<Person> person = new ComboBox<>("Person");
    ComboBox<MeasurementType> type = new ComboBox<>("Measurement Type");
    NumberField targetValue = new NumberField("Target Value");
    TextField unit = new TextField("Unit");
    DatePicker targetDate = new DatePicker("Target Date");
    Checkbox achieved = new Checkbox("Achieved");
    TextArea notes = new TextArea("Notes");
    
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button cancel = new Button("Cancel");
    
    Binder<MeasurementGoal> binder = new BeanValidationBinder<>(MeasurementGoal.class);
    
    public MeasurementGoalForm(MeasurementGoalService goalService, PersonService personService) {
        this.goalService = goalService;
        this.personService = personService;
        
        addClassName("goal-form");
        
        // Configure form fields
        configureFormFields();
        
        // Configure form fields validation
        binder.bindInstanceFields(this);
        
        add(
            person,
            type,
            targetValue,
            unit,
            targetDate,
            achieved,
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
        
        // Set default target date as 1 month from now
        targetDate.setValue(LocalDate.now().plusMonths(1));
        
        // Configure auto-fill of unit based on selected type
        type.addValueChangeListener(e -> {
            if (e.getValue() != null) {
                unit.setValue(e.getValue().getDefaultUnit());
            }
        });
        
        notes.setPlaceholder("Additional information about the goal");
    }
    
    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        
        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);
        
        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, goal)));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));
        
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        
        return new HorizontalLayout(save, delete, cancel);
    }
    
    public void setGoal(MeasurementGoal goal) {
        this.goal = goal;
        binder.readBean(goal);
    }
    
    private void validateAndSave() {
        try {
            binder.writeBean(goal);
            fireEvent(new SaveEvent(this, goal));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
    
    // Events
    public static abstract class GoalFormEvent extends ComponentEvent<MeasurementGoalForm> {
        private final MeasurementGoal goal;
        
        protected GoalFormEvent(MeasurementGoalForm source, MeasurementGoal goal) {
            super(source, false);
            this.goal = goal;
        }
        
        public MeasurementGoal getGoal() {
            return goal;
        }
    }
    
    public static class SaveEvent extends GoalFormEvent {
        SaveEvent(MeasurementGoalForm source, MeasurementGoal goal) {
            super(source, goal);
        }
    }
    
    public static class DeleteEvent extends GoalFormEvent {
        DeleteEvent(MeasurementGoalForm source, MeasurementGoal goal) {
            super(source, goal);
        }
    }
    
    public static class CloseEvent extends ComponentEvent<MeasurementGoalForm> {
        CloseEvent(MeasurementGoalForm source) {
            super(source, false);
        }
    }
    
    @Override
    public <T extends ComponentEvent<?>> Registration addListener(
            Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
} 