package com.example.healthtracker.ui;

import com.example.healthtracker.model.Person;
import com.example.healthtracker.service.PersonService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PreserveOnRefresh;
import jakarta.annotation.security.PermitAll;

@Route(value = "/persons", layout = MainLayout.class)
@PageTitle("Persons | Health Tracker")
@PreserveOnRefresh
@PermitAll
public class PersonsView extends VerticalLayout {
    
    private final PersonService personService;
    private final Grid<Person> grid = new Grid<>(Person.class);
    private final TextField filterField = new TextField();
    private final PersonForm form;
    
    public PersonsView(PersonService personService) {
        this.personService = personService;
        this.form = new PersonForm(personService);
        
        addClassName("persons-view");
        setSizeFull();
        
        configureGrid();
        configureForm();
        
        add(
            getToolbar(),
            getContent()
        );
        
        updateGrid();
        closeEditor();
    }
    
    private void configureGrid() {
        grid.addClassName("persons-grid");
        grid.setSizeFull();
        grid.setColumns("firstName", "lastName", "birthDate", "gender", "email", "phoneNumber");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        
        grid.asSingleSelect().addValueChangeListener(e -> editPerson(e.getValue()));
    }
    
    private Component getToolbar() {
        filterField.setPlaceholder("Filter by name...");
        filterField.setClearButtonVisible(true);
        filterField.setValueChangeMode(ValueChangeMode.LAZY);
        filterField.addValueChangeListener(e -> updateGrid());
        
        Button addPersonButton = new Button("Add Person");
        addPersonButton.addClickListener(e -> addPerson());
        
        HorizontalLayout toolbar = new HorizontalLayout(filterField, addPersonButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }
    
    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }
    
    private void configureForm() {
        form.setWidth("25em");
        
        form.addListener(PersonForm.SaveEvent.class, this::savePerson);
        form.addListener(PersonForm.DeleteEvent.class, this::deletePerson);
        form.addListener(PersonForm.CloseEvent.class, e -> closeEditor());
    }
    
    private void updateGrid() {
        grid.setItems(filterField.isEmpty() 
                ? personService.findAllPersons() 
                : personService.findPersonsByName(filterField.getValue()));
    }
    
    private void savePerson(PersonForm.SaveEvent event) {
        personService.savePerson(event.getPerson());
        updateGrid();
        closeEditor();
        Notification.show("Person details saved", 3000, Notification.Position.BOTTOM_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
    
    private void deletePerson(PersonForm.DeleteEvent event) {
        try {
            personService.deletePerson(event.getPerson().getId());
            updateGrid();
            closeEditor();
            Notification.show("Person deleted", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification.show("Cannot delete person: " + ex.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void addPerson() {
        grid.asSingleSelect().clear();
        editPerson(new Person());
    }
    
    private void editPerson(Person person) {
        if (person == null) {
            closeEditor();
        } else {
            form.setPerson(person);
            form.setVisible(true);
            addClassName("editing");
        }
    }
    
    private void closeEditor() {
        form.setPerson(null);
        form.setVisible(false);
        removeClassName("editing");
    }
} 