package com.example.healthtracker.ui;

import com.example.healthtracker.model.MeasurementGoal;
import com.example.healthtracker.model.Measurement.MeasurementType;
import com.example.healthtracker.model.Person;
import com.example.healthtracker.service.MeasurementGoalService;
import com.example.healthtracker.service.PersonService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "/goals", layout = MainLayout.class)
@PageTitle("Health Goals | Health Tracker")
@PreserveOnRefresh
@PermitAll
public class GoalsView extends VerticalLayout {
    
    private final MeasurementGoalService goalService;
    private final PersonService personService;
    private final Grid<MeasurementGoal> grid = new Grid<>(MeasurementGoal.class);
    private final ComboBox<Person> personFilter = new ComboBox<>("Person");
    private final ComboBox<MeasurementType> typeFilter = new ComboBox<>("Measurement Type");
    private final ComboBox<Boolean> achievedFilter = new ComboBox<>("Status");
    private final MeasurementGoalForm form;
    
    public GoalsView(MeasurementGoalService goalService, PersonService personService) {
        this.goalService = goalService;
        this.personService = personService;
        this.form = new MeasurementGoalForm(goalService, personService);
        
        addClassName("goals-view");
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
        grid.addClassName("goals-grid");
        grid.setSizeFull();
        grid.removeAllColumns();
        
        // Add custom column for person name
        grid.addColumn(goal -> 
            goal.getPerson().getFirstName() + " " + goal.getPerson().getLastName())
            .setHeader("Person")
            .setAutoWidth(true);
            
        grid.addColumn(MeasurementGoal::getType).setHeader("Type").setAutoWidth(true);
        grid.addColumn(MeasurementGoal::getTargetValue).setHeader("Target Value").setAutoWidth(true);
        grid.addColumn(MeasurementGoal::getUnit).setHeader("Unit").setAutoWidth(true);
        grid.addColumn(goal -> goal.getTargetDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
            .setHeader("Target Date")
            .setAutoWidth(true);
        
        // Custom status column with visual indicator
        grid.addComponentColumn(goal -> {
            Span status = new Span(goal.isAchieved() ? "Achieved" : "In Progress");
            status.getElement().getThemeList().add(
                goal.isAchieved() ? "badge success" : "badge");
            return status;
        }).setHeader("Status").setAutoWidth(true);
        
        grid.addColumn(MeasurementGoal::getNotes).setHeader("Notes").setAutoWidth(true);
        
        grid.asSingleSelect().addValueChangeListener(e -> editGoal(e.getValue()));
    }
    
    private Component getToolbar() {
        // Configure person filter
        List<Person> persons = personService.findAllPersons();
        personFilter.setItems(persons);
        personFilter.setItemLabelGenerator(p -> p.getFirstName() + " " + p.getLastName());
        personFilter.setClearButtonVisible(true);
        personFilter.addValueChangeListener(e -> updateGrid());
        
        // Configure type filter
        typeFilter.setItems(MeasurementType.values());
        typeFilter.setItemLabelGenerator(MeasurementType::name);
        typeFilter.setClearButtonVisible(true);
        typeFilter.addValueChangeListener(e -> updateGrid());
        
        // Configure achieved filter
        achievedFilter.setItems(true, false);
        achievedFilter.setItemLabelGenerator(achieved -> achieved ? "Achieved" : "In Progress");
        achievedFilter.setClearButtonVisible(true);
        achievedFilter.addValueChangeListener(e -> updateGrid());
        
        Button addGoalButton = new Button("Add Goal");
        addGoalButton.addClickListener(e -> addGoal());
        
        Button checkGoalsButton = new Button("Check Goal Status");
        checkGoalsButton.addClickListener(e -> checkGoalStatus());
        
        HorizontalLayout toolbar = new HorizontalLayout(
            personFilter, typeFilter, achievedFilter, addGoalButton, checkGoalsButton);
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
        
        form.addListener(MeasurementGoalForm.SaveEvent.class, this::saveGoal);
        form.addListener(MeasurementGoalForm.DeleteEvent.class, this::deleteGoal);
        form.addListener(MeasurementGoalForm.CloseEvent.class, e -> closeEditor());
    }
    
    private void updateGrid() {
        List<MeasurementGoal> goals;
        
        Person selectedPerson = personFilter.getValue();
        MeasurementType selectedType = typeFilter.getValue();
        Boolean selectedStatus = achievedFilter.getValue();
        
        if (selectedPerson != null) {
            if (selectedType != null) {
                goals = goalService.findGoalsByPersonIdAndType(selectedPerson.getId(), selectedType);
            } else if (selectedStatus != null) {
                goals = goalService.findGoalsByPersonIdAndAchievementStatus(selectedPerson.getId(), selectedStatus);
            } else {
                goals = goalService.findGoalsByPersonId(selectedPerson.getId());
            }
        } else {
            goals = goalService.findAllGoals();
            
            // Apply type filter if selected
            if (selectedType != null) {
                goals = goals.stream()
                        .filter(g -> g.getType() == selectedType)
                        .toList();
            }
            
            // Apply status filter if selected
            if (selectedStatus != null) {
                goals = goals.stream()
                        .filter(g -> g.isAchieved() == selectedStatus)
                        .toList();
            }
        }
        
        grid.setItems(goals);
    }
    
    private void saveGoal(MeasurementGoalForm.SaveEvent event) {
        goalService.saveGoal(event.getGoal());
        updateGrid();
        closeEditor();
        Notification.show("Goal saved", 3000, Notification.Position.BOTTOM_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
    
    private void deleteGoal(MeasurementGoalForm.DeleteEvent event) {
        try {
            goalService.deleteGoal(event.getGoal().getId());
            updateGrid();
            closeEditor();
            Notification.show("Goal deleted", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification.show("Cannot delete goal: " + ex.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void addGoal() {
        grid.asSingleSelect().clear();
        MeasurementGoal goal = new MeasurementGoal();
        if (personFilter.getValue() != null) {
            goal.setPerson(personFilter.getValue());
        }
        if (typeFilter.getValue() != null) {
            goal.setType(typeFilter.getValue());
            goal.setUnit(typeFilter.getValue().getDefaultUnit());
        }
        editGoal(goal);
    }
    
    private void editGoal(MeasurementGoal goal) {
        if (goal == null) {
            closeEditor();
        } else {
            form.setGoal(goal);
            form.setVisible(true);
            addClassName("editing");
        }
    }
    
    private void closeEditor() {
        form.setGoal(null);
        form.setVisible(false);
        removeClassName("editing");
    }
    
    private void checkGoalStatus() {
        Person selectedPerson = personFilter.getValue();
        if (selectedPerson != null) {
            goalService.checkAndUpdateGoalStatus(selectedPerson.getId());
            updateGrid();
            Notification.show("Goal status updated", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } else {
            Notification.show("Please select a person", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
} 