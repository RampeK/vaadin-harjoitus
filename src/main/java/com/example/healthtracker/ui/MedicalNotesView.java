package com.example.healthtracker.ui;

import com.example.healthtracker.model.MedicalNote;
import com.example.healthtracker.model.MedicalNote.NoteCategory;
import com.example.healthtracker.model.Person;
import com.example.healthtracker.service.MedicalNoteService;
import com.example.healthtracker.service.PersonService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.annotation.security.PermitAll;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Route(value = "medical-notes", layout = MainLayout.class)
@PageTitle("Medical Notes | Health Tracker")
@PermitAll
@PreserveOnRefresh
public class MedicalNotesView extends VerticalLayout {

    private final MedicalNoteService medicalNoteService;
    private final PersonService personService;
    
    private Grid<MedicalNote> grid = new Grid<>(MedicalNote.class);
    private TextField filterText = new TextField();
    private ComboBox<Person> personFilter = new ComboBox<>("Person");
    private ComboBox<NoteCategory> categoryFilter = new ComboBox<>("Category");
    
    private MedicalNoteForm form;
    
    private boolean isAdmin;

    public MedicalNotesView(MedicalNoteService medicalNoteService, PersonService personService) {
        this.medicalNoteService = medicalNoteService;
        this.personService = personService;
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        this.isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        
        addClassName("medical-notes-view");
        setSizeFull();
        
        configureGrid();
        configureForm();
        
        add(
            getToolbar(),
            getContent()
        );
        
        updateList();
        closeEditor();
    }
    
    private void configureGrid() {
        grid.addClassName("medical-notes-grid");
        grid.setSizeFull();
        grid.removeAllColumns();
        
        // Define columns for the grid
        grid.addColumn(note -> note.getPerson() != null ? 
                note.getPerson().getFirstName() + " " + note.getPerson().getLastName() : "")
                .setHeader("Person").setSortable(true);
        
        grid.addColumn(MedicalNote::getTitle).setHeader("Title").setSortable(true);
        
        // Category with color badge
        grid.addColumn(new ComponentRenderer<>(note -> {
            Span badge = new Span(note.getCategory().getDisplayName());
            String color;
            
            // Switch based on enum
            switch (note.getCategory()) {
                case GENERAL:
                    color = "badge";
                    break;
                case DIAGNOSIS:
                    color = "badge error";
                    break;
                case TREATMENT:
                    color = "badge success";
                    break;
                case FOLLOWUP:
                    color = "badge contrast";
                    break;
                case MEDICATION:
                    color = "badge primary";
                    break;
                default:
                    color = "badge";
                    break;
            }
            
            badge.getElement().getThemeList().add(color);
            return badge;
        })).setHeader("Category").setSortable(true);
        
        // Date column with simple formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        grid.addColumn(note -> note.getNoteDate().format(formatter))
            .setHeader("Date")
            .setSortable(true);
            
        grid.addColumn(MedicalNote::getDoctorName).setHeader("Doctor").setSortable(true);
        
        // Content preview
        grid.addColumn(note -> {
            String content = note.getContent();
            if (content != null && content.length() > 50) {
                return content.substring(0, 47) + "...";
            }
            return content;
        }).setHeader("Content").setFlexGrow(1);
        
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        
        // Only show note details to admins or if the current user is a doctor
        grid.setItemDetailsRenderer(new ComponentRenderer<>(note -> {
            if (isAdmin || note.getDoctorName().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
                VerticalLayout layout = new VerticalLayout();
                layout.add(new Span("Full content:"));
                Div content = new Div();
                content.setText(note.getContent());
                content.addClassName(LumoUtility.Padding.MEDIUM);
                content.addClassName(LumoUtility.Border.ALL);
                content.addClassName(LumoUtility.BorderRadius.MEDIUM);
                layout.add(content);
                return layout;
            } else {
                return new Span("You don't have permission to view full details");
            }
        }));
        
        grid.asSingleSelect().addValueChangeListener(e -> editNote(e.getValue()));
    }
    
    private Component getToolbar() {
        filterText.setPlaceholder("Filter by title...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());
        
        personFilter.setItems(personService.findAllPersons());
        personFilter.setItemLabelGenerator(person -> 
            person.getFirstName() + " " + person.getLastName());
        personFilter.setClearButtonVisible(true);
        personFilter.addValueChangeListener(e -> updateList());
        
        categoryFilter.setItems(NoteCategory.values());
        categoryFilter.setItemLabelGenerator(NoteCategory::getDisplayName);
        categoryFilter.setClearButtonVisible(true);
        categoryFilter.addValueChangeListener(e -> updateList());
        
        Button addNoteButton = new Button("Add Medical Note");
        addNoteButton.addClickListener(e -> addNote());
        
        HorizontalLayout toolbar = new HorizontalLayout(
            filterText, 
            personFilter,
            categoryFilter,
            addNoteButton
        );
        
        toolbar.addClassName("toolbar");
        return toolbar;
    }
    
    private void configureForm() {
        form = new MedicalNoteForm(medicalNoteService, personService);
        form.setWidth("25em");
        
        form.addListener(MedicalNoteForm.SaveEvent.class, this::saveNote);
        form.addListener(MedicalNoteForm.DeleteEvent.class, this::deleteNote);
        form.addListener(MedicalNoteForm.CloseEvent.class, e -> closeEditor());
    }
    
    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }
    
    private void updateList() {
        List<MedicalNote> notes;
        
        if (isAdmin) {
            // Admin can see all notes, but still filter them
            notes = medicalNoteService.findAllNotes();
        } else {
            // Regular users can only see notes where they are either the doctor or the person
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            
            // Get the person IDs associated with the current user
            Set<Long> userPersonIds = personService.findAllPersons().stream()
                .filter(p -> p.getFirstName().equalsIgnoreCase(username) || 
                        p.getLastName().equalsIgnoreCase(username) ||
                        (p.getFirstName() + " " + p.getLastName()).equalsIgnoreCase(username))
                .map(Person::getId)
                .collect(Collectors.toSet());
                
            notes = medicalNoteService.findAllNotes().stream()
                .filter(note -> 
                    // User is the doctor
                    note.getDoctorName().equals(username) ||
                    // User is the person
                    (note.getPerson() != null && userPersonIds.contains(note.getPerson().getId())))
                .collect(Collectors.toList());
        }
        
        // Apply filters
        if (filterText.getValue() != null && !filterText.getValue().isEmpty()) {
            String filter = filterText.getValue().toLowerCase();
            notes = notes.stream()
                .filter(note -> 
                    note.getTitle().toLowerCase().contains(filter) ||
                    note.getContent().toLowerCase().contains(filter) ||
                    note.getDoctorName().toLowerCase().contains(filter))
                .collect(Collectors.toList());
        }
        
        if (personFilter.getValue() != null) {
            Long personId = personFilter.getValue().getId();
            notes = notes.stream()
                .filter(note -> note.getPerson() != null && note.getPerson().getId().equals(personId))
                .collect(Collectors.toList());
        }
        
        if (categoryFilter.getValue() != null) {
            NoteCategory category = categoryFilter.getValue();
            notes = notes.stream()
                .filter(note -> note.getCategory() == category)
                .collect(Collectors.toList());
        }
        
        grid.setItems(notes);
    }
    
    private void addNote() {
        closeEditor();
        MedicalNote note = new MedicalNote();
        // Set current user as doctor
        note.setDoctorName(SecurityContextHolder.getContext().getAuthentication().getName());
        editNote(note);
    }
    
    private void editNote(MedicalNote note) {
        if (note == null) {
            closeEditor();
        } else {
            form.setMedicalNote(note);
            form.setVisible(true);
            addClassName("editing");
        }
    }
    
    private void saveNote(MedicalNoteForm.SaveEvent event) {
        // First check permissions
        MedicalNote note = event.getMedicalNote();
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Only admin or the doctor who created the note can update it
        if (isAdmin || note.getDoctorName().equals(currentUser)) {
            MedicalNote savedNote = medicalNoteService.saveNote(note);
            updateList();
            Notification.show("Medical note details saved successfully.", 3000, Notification.Position.BOTTOM_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            closeEditor();
        } else {
            Notification.show("You don't have permission to edit this note.", 3000, Notification.Position.BOTTOM_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void deleteNote(MedicalNoteForm.DeleteEvent event) {
        // First check permissions
        MedicalNote note = event.getMedicalNote();
        String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
        
        // Only admin or the doctor who created the note can delete it
        if (isAdmin || note.getDoctorName().equals(currentUser)) {
            medicalNoteService.deleteNote(note.getId());
            updateList();
            Notification.show("Medical note deleted.", 3000, Notification.Position.BOTTOM_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            closeEditor();
        } else {
            Notification.show("You don't have permission to delete this note.", 3000, Notification.Position.BOTTOM_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void closeEditor() {
        form.setMedicalNote(null);
        form.setVisible(false);
        removeClassName("editing");
    }
} 