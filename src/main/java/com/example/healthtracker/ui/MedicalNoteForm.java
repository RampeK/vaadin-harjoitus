package com.example.healthtracker.ui;

import com.example.healthtracker.model.MedicalNote;
import com.example.healthtracker.model.MedicalNote.NoteCategory;
import com.example.healthtracker.model.Person;
import com.example.healthtracker.service.MedicalNoteService;
import com.example.healthtracker.service.PersonService;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDateTime;
import java.util.List;

public class MedicalNoteForm extends FormLayout {
    
    private MedicalNote medicalNote;
    
    private TextField title = new TextField("Title");
    private ComboBox<Person> person = new ComboBox<>("Person");
    private DateTimePicker noteDate = new DateTimePicker("Date");
    private ComboBox<NoteCategory> category = new ComboBox<>("Category");
    private TextField doctorName = new TextField("Doctor's Name");
    private TextArea content = new TextArea("Content");
    
    private Button save = new Button("Save");
    private Button delete = new Button("Delete");
    private Button close = new Button("Cancel");
    
    private Binder<MedicalNote> binder = new BeanValidationBinder<>(MedicalNote.class);
    
    public MedicalNoteForm(MedicalNoteService medicalNoteService, PersonService personService) {
        addClassName("medical-note-form");
        
        // Configure form fields
        title.setRequired(true);
        person.setRequired(true);
        noteDate.setMin(LocalDateTime.now().minusYears(1));
        noteDate.setValue(LocalDateTime.now());
        
        // Set the items for ComboBoxes
        List<Person> persons = personService.findAllPersons();
        person.setItems(persons);
        person.setItemLabelGenerator(p -> p.getFirstName() + " " + p.getLastName());
        
        category.setItems(NoteCategory.values());
        category.setItemLabelGenerator(NoteCategory::getDisplayName);
        
        content.setHeight("150px");
        
        // Configure buttons
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        
        // Add save, delete, and close event listeners
        save.addClickListener(event -> validateAndSave());
        delete.addClickListener(event -> fireEvent(new DeleteEvent(this, medicalNote)));
        close.addClickListener(event -> fireEvent(new CloseEvent(this)));
        
        // Configure binder
        binder.bindInstanceFields(this);
        
        // Add form components
        add(
            title,
            person,
            category,
            noteDate,
            doctorName,
            content,
            createButtonsLayout()
        );
    }
    
    private HorizontalLayout createButtonsLayout() {
        save.addClickShortcut(com.vaadin.flow.component.Key.ENTER);
        close.addClickShortcut(com.vaadin.flow.component.Key.ESCAPE);
        
        HorizontalLayout buttonsLayout = new HorizontalLayout(save, delete, close);
        buttonsLayout.addClassName("buttons-layout");
        return buttonsLayout;
    }
    
    public void setMedicalNote(MedicalNote medicalNote) {
        this.medicalNote = medicalNote;
        binder.readBean(medicalNote);
    }
    
    private void validateAndSave() {
        try {
            binder.writeBean(medicalNote);
            fireEvent(new SaveEvent(this, medicalNote));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Events
    public static abstract class MedicalNoteFormEvent extends ComponentEvent<MedicalNoteForm> {
        private MedicalNote medicalNote;
        
        protected MedicalNoteFormEvent(MedicalNoteForm source, MedicalNote medicalNote) {
            super(source, false);
            this.medicalNote = medicalNote;
        }
        
        public MedicalNote getMedicalNote() {
            return medicalNote;
        }
    }
    
    public static class SaveEvent extends MedicalNoteFormEvent {
        SaveEvent(MedicalNoteForm source, MedicalNote medicalNote) {
            super(source, medicalNote);
        }
    }
    
    public static class DeleteEvent extends MedicalNoteFormEvent {
        DeleteEvent(MedicalNoteForm source, MedicalNote medicalNote) {
            super(source, medicalNote);
        }
    }
    
    public static class CloseEvent extends MedicalNoteFormEvent {
        CloseEvent(MedicalNoteForm source) {
            super(source, null);
        }
    }
    
    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType, ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
} 