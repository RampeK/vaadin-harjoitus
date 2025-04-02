package com.example.healthtracker.ui;

import com.example.healthtracker.model.Measurement;
import com.example.healthtracker.model.Measurement.MeasurementType;
import com.example.healthtracker.model.Person;
import com.example.healthtracker.service.MeasurementService;
import com.example.healthtracker.service.PersonService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.html.Span;
import jakarta.annotation.security.PermitAll;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Route(value = "/measurements", layout = MainLayout.class)
@PageTitle("Measurements | Health Tracker")
@PreserveOnRefresh
@PermitAll
public class MeasurementsView extends VerticalLayout {
    
    private final MeasurementService measurementService;
    private final PersonService personService;
    private final Grid<Measurement> grid = new Grid<>(Measurement.class);
    private final ComboBox<Person> personFilter = new ComboBox<>("Person");
    private final ComboBox<MeasurementType> typeFilter = new ComboBox<>("Measurement Type");
    private final DatePicker startDateFilter = new DatePicker("From Date");
    private final DatePicker endDateFilter = new DatePicker("To Date");
    private final MeasurementForm form;
    private ScheduledExecutorService executorService;
    
    public MeasurementsView(MeasurementService measurementService, PersonService personService) {
        this.measurementService = measurementService;
        this.personService = personService;
        this.form = new MeasurementForm(measurementService, personService);
        
        addClassName("measurements-view");
        setSizeFull();
        
        configureGrid();
        configureForm();
        
        add(
            getToolbar(),
            getContent()
        );
        
        updateGrid();
        closeEditor();
        
        // Setup automatic refresh using server push
        setupAutomaticRefresh();
    }
    
    private void setupAutomaticRefresh() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        
        executorService.scheduleAtFixedRate(() -> {
            getUI().ifPresent(ui -> {
                ui.access(() -> {
                    updateGrid();
                    Notification.show("Measurements data refreshed", 
                            2000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                });
            });
        }, 30, 30, TimeUnit.SECONDS);
    }
    
    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Clean up the executor service when the view is detached
        if (executorService != null) {
            executorService.shutdown();
        }
        super.onDetach(detachEvent);
    }
    
    private void configureGrid() {
        grid.addClassName("measurements-grid");
        grid.setSizeFull();
        grid.removeAllColumns();
        
        grid.addColumn(measurement -> 
            measurement.getPerson().getFirstName() + " " + measurement.getPerson().getLastName())
            .setHeader("Person")
            .setAutoWidth(true);
            
        grid.addColumn(Measurement::getType).setHeader("Type").setAutoWidth(true);
        
        grid.addComponentColumn(measurement -> {
            Double value = measurement.getMeasurementValue();
            MeasurementType type = measurement.getType();
            
            String formattedValue = String.format("%.2f", value);
            Span valueSpan = new Span(formattedValue + " " + measurement.getUnit());
            
            switch (type) {
                case BLOOD_PRESSURE:
                    if (value > 140.0) {
                        valueSpan.getElement().getThemeList().add("badge error");
                        valueSpan.getElement().setAttribute("title", "High blood pressure");
                    } else if (value < 90.0) {
                        valueSpan.getElement().getThemeList().add("badge contrast");
                        valueSpan.getElement().setAttribute("title", "Low blood pressure");
                    } else {
                        valueSpan.getElement().getThemeList().add("badge success");
                    }
                    break;
                    
                case HEART_RATE:
                    if (value > 100.0) {
                        valueSpan.getElement().getThemeList().add("badge error");
                        valueSpan.getElement().setAttribute("title", "High heart rate");
                    } else if (value < 60.0) {
                        valueSpan.getElement().getThemeList().add("badge contrast");
                        valueSpan.getElement().setAttribute("title", "Low heart rate");
                    } else {
                        valueSpan.getElement().getThemeList().add("badge success");
                    }
                    break;
                    
                case WEIGHT:
                    valueSpan.getElement().getThemeList().add("badge");
                    break;
                    
                case HEIGHT:
                    valueSpan.getElement().getThemeList().add("badge");
                    break;
                    
                case TEMPERATURE:
                    if (value > 37.5) {
                        valueSpan.getElement().getThemeList().add("badge error");
                        valueSpan.getElement().setAttribute("title", "Fever");
                    } else if (value < 36.0) {
                        valueSpan.getElement().getThemeList().add("badge contrast");
                        valueSpan.getElement().setAttribute("title", "Low temperature");
                    } else {
                        valueSpan.getElement().getThemeList().add("badge success");
                    }
                    break;
                    
                case BLOOD_SUGAR:
                    if (value > 7.0) {
                        valueSpan.getElement().getThemeList().add("badge error");
                        valueSpan.getElement().setAttribute("title", "High blood sugar");
                    } else if (value < 3.9) {
                        valueSpan.getElement().getThemeList().add("badge contrast");
                        valueSpan.getElement().setAttribute("title", "Low blood sugar");
                    } else {
                        valueSpan.getElement().getThemeList().add("badge success");
                    }
                    break;
                    
                default:
                    valueSpan.getElement().getThemeList().add("badge");
                    break;
            }
            
            return valueSpan;
        })
        .setHeader("Value")
        .setAutoWidth(true);
        
        grid.addColumn(Measurement::getMeasuredAt).setHeader("Measured At").setAutoWidth(true);
        grid.addColumn(Measurement::getNotes).setHeader("Notes").setAutoWidth(true);
        
        grid.asSingleSelect().addValueChangeListener(e -> editMeasurement(e.getValue()));
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
        
        // Configure date filters with default values and listeners
        startDateFilter.setClearButtonVisible(true);
        startDateFilter.addValueChangeListener(e -> {
            // Set minimum date for end date filter
            if (e.getValue() != null) {
                endDateFilter.setMin(e.getValue());
            } else {
                endDateFilter.setMin(null);
            }
            updateGrid();
        });
        
        endDateFilter.setClearButtonVisible(true);
        endDateFilter.addValueChangeListener(e -> {
            // Set maximum date for start date filter
            if (e.getValue() != null) {
                startDateFilter.setMax(e.getValue());
            } else {
                startDateFilter.setMax(null);
            }
            updateGrid();
        });
        
        Button addMeasurementButton = new Button("Add Measurement");
        addMeasurementButton.addClickListener(e -> addMeasurement());
        
        HorizontalLayout filterLayout = new HorizontalLayout(
            personFilter, typeFilter, startDateFilter, endDateFilter);
        filterLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        
        HorizontalLayout toolbar = new HorizontalLayout(filterLayout, addMeasurementButton);
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        toolbar.setWidthFull();
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
        
        form.addListener(MeasurementForm.SaveEvent.class, this::saveMeasurement);
        form.addListener(MeasurementForm.DeleteEvent.class, this::deleteMeasurement);
        form.addListener(MeasurementForm.CloseEvent.class, e -> closeEditor());
    }
    
    private void updateGrid() {
        List<Measurement> measurements;
        
        Person selectedPerson = personFilter.getValue();
        MeasurementType selectedType = typeFilter.getValue();
        LocalDate startDate = startDateFilter.getValue();
        LocalDate endDate = endDateFilter.getValue();
        
        // Convert LocalDate to LocalDateTime for filtering
        LocalDateTime startDateTime = startDate != null 
            ? LocalDateTime.of(startDate, LocalTime.MIN) 
            : null;
        LocalDateTime endDateTime = endDate != null 
            ? LocalDateTime.of(endDate, LocalTime.MAX) 
            : null;
        
        // Apply filters based on selected criteria
        if (selectedPerson != null) {
            if (selectedType != null && startDateTime != null && endDateTime != null) {
                // Filter by person, type, and date range
                measurements = measurementService.findMeasurementsByPersonIdAndTypeAndDateRange(
                    selectedPerson.getId(), selectedType, startDateTime, endDateTime);
            } else if (startDateTime != null && endDateTime != null) {
                // Filter by person and date range
                measurements = measurementService.findMeasurementsByPersonIdAndDateRange(
                    selectedPerson.getId(), startDateTime, endDateTime);
            } else if (selectedType != null) {
                // Filter by person and type
                measurements = measurementService.findMeasurementsByPersonIdAndType(
                    selectedPerson.getId(), selectedType);
            } else {
                // Filter by person only
                measurements = measurementService.findMeasurementsByPersonId(
                    selectedPerson.getId());
            }
        } else {
            // No person filter, get all measurements
            measurements = measurementService.findAllMeasurements();
            
            // Apply type filter if selected
            if (selectedType != null) {
                measurements = measurements.stream()
                    .filter(m -> m.getType() == selectedType)
                    .toList();
            }
            
            // Apply date range filter if selected
            if (startDateTime != null && endDateTime != null) {
                measurements = measurements.stream()
                    .filter(m -> {
                        LocalDateTime measuredAt = m.getMeasuredAt();
                        return !measuredAt.isBefore(startDateTime) && !measuredAt.isAfter(endDateTime);
                    })
                    .toList();
            }
        }
        
        // Apply additional date sorting (most recent first)
        measurements = measurements.stream()
                .sorted((m1, m2) -> m2.getMeasuredAt().compareTo(m1.getMeasuredAt()))
                .toList();
        
        grid.setItems(measurements);
    }
    
    private void saveMeasurement(MeasurementForm.SaveEvent event) {
        measurementService.saveMeasurement(event.getMeasurement());
        updateGrid();
        closeEditor();
        Notification.show("Measurement saved", 3000, Notification.Position.BOTTOM_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
    
    private void deleteMeasurement(MeasurementForm.DeleteEvent event) {
        try {
            measurementService.deleteMeasurement(event.getMeasurement().getId());
            updateGrid();
            closeEditor();
            Notification.show("Measurement deleted", 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            Notification.show("Cannot delete measurement: " + ex.getMessage(), 3000, Notification.Position.BOTTOM_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private void addMeasurement() {
        grid.asSingleSelect().clear();
        Measurement measurement = new Measurement();
        if (personFilter.getValue() != null) {
            measurement.setPerson(personFilter.getValue());
        }
        if (typeFilter.getValue() != null) {
            measurement.setType(typeFilter.getValue());
            measurement.setUnit(typeFilter.getValue().getDefaultUnit());
        }
        editMeasurement(measurement);
    }
    
    private void editMeasurement(Measurement measurement) {
        if (measurement == null) {
            closeEditor();
        } else {
            form.setMeasurement(measurement);
            form.setVisible(true);
            addClassName("editing");
        }
    }
    
    private void closeEditor() {
        form.setMeasurement(null);
        form.setVisible(false);
        removeClassName("editing");
    }
} 