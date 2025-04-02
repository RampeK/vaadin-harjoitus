package com.example.healthtracker.ui;

import com.example.healthtracker.model.Person;
import com.example.healthtracker.model.Person.Gender;
import com.example.healthtracker.service.PersonService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class PersonForm extends FormLayout {
    
    private final PersonService personService;
    
    private Person person;
    
    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    DatePicker birthDate = new DatePicker("Birth date");
    ComboBox<Gender> gender = new ComboBox<>("Gender");
    EmailField email = new EmailField("Email");
    TextField phoneNumber = new TextField("Phone number");
    
    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button cancel = new Button("Cancel");
    
    Binder<Person> binder = new BeanValidationBinder<>(Person.class);
    
    public PersonForm(PersonService personService) {
        this.personService = personService;
        
        addClassName("person-form");
        
        // Configure gender dropdown
        gender.setItems(Gender.values());
        gender.setItemLabelGenerator(Gender::name);
        
        // Configure form fields validation
        binder.bindInstanceFields(this);
        
        add(
            firstName,
            lastName,
            birthDate,
            gender,
            email,
            phoneNumber,
            createButtonsLayout()
        );
    }
    
    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        
        save.addClickShortcut(Key.ENTER);
        cancel.addClickShortcut(Key.ESCAPE);
        
        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, person)));
        cancel.addClickListener(event -> fireEvent(new CloseEvent(this)));
        
        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));
        
        return new HorizontalLayout(save, delete, cancel);
    }
    
    public void setPerson(Person person) {
        this.person = person;
        binder.readBean(person);
    }
    
    private void validateAndSave() {
        try {
            binder.writeBean(person);
            fireEvent(new SaveEvent(this, person));
        } catch (ValidationException e) {
            e.printStackTrace();
        }
    }
    
    // Events
    public static abstract class PersonFormEvent extends ComponentEvent<PersonForm> {
        private final Person person;
        
        protected PersonFormEvent(PersonForm source, Person person) {
            super(source, false);
            this.person = person;
        }
        
        public Person getPerson() {
            return person;
        }
    }
    
    public static class SaveEvent extends PersonFormEvent {
        SaveEvent(PersonForm source, Person person) {
            super(source, person);
        }
    }
    
    public static class DeleteEvent extends PersonFormEvent {
        DeleteEvent(PersonForm source, Person person) {
            super(source, person);
        }
    }
    
    public static class CloseEvent extends ComponentEvent<PersonForm> {
        CloseEvent(PersonForm source) {
            super(source, false);
        }
    }
    
    @Override
    public <T extends ComponentEvent<?>> Registration addListener(
            Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
} 