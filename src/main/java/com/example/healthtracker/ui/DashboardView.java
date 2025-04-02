package com.example.healthtracker.ui;

import com.example.healthtracker.model.Measurement;
import com.example.healthtracker.model.Measurement.MeasurementType;
import com.example.healthtracker.model.Person;
import com.example.healthtracker.service.MeasurementService;
import com.example.healthtracker.service.PersonService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Route(value = "/dashboard", layout = MainLayout.class)
@PageTitle("Dashboard | Health Tracker")
@PreserveOnRefresh
@PermitAll
public class DashboardView extends VerticalLayout {
    
    private final PersonService personService;
    private final MeasurementService measurementService;
    private VerticalLayout recentMeasurementsLayout;
    private ScheduledExecutorService executorService;
    
    public DashboardView(PersonService personService, MeasurementService measurementService) {
        this.personService = personService;
        this.measurementService = measurementService;
        
        addClassName("dashboard-view");
        setSpacing(false);
        
        add(
            createHeader(),
            createSummaryStats(),
            createRecentMeasurements()
        );
        
        // Setup real-time updates with server push
        setupRealtimeUpdates();
    }
    
    private void setupRealtimeUpdates() {
        executorService = Executors.newSingleThreadScheduledExecutor();
        
        executorService.scheduleAtFixedRate(() -> {
            getUI().ifPresent(ui -> {
                ui.access(() -> {
                    updateRecentMeasurements();
                    Notification.show("Dashboard updated with latest measurements", 
                            2000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
                });
            });
        }, 15, 15, TimeUnit.SECONDS);
    }
    
    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Clean up the executor service when the view is detached
        if (executorService != null) {
            executorService.shutdown();
        }
        super.onDetach(detachEvent);
    }
    
    private Component createHeader() {
        H2 header = new H2("Health Tracker Dashboard");
        header.addClassNames(
            LumoUtility.Margin.Top.XLARGE,
            LumoUtility.Margin.Bottom.MEDIUM
        );
        
        Paragraph description = new Paragraph("Welcome to the Health Tracker application. Here you can track your health measurements and monitor your health progress.");
        description.addClassNames(
            LumoUtility.Margin.Bottom.XLARGE
        );
        
        return new VerticalLayout(header, description);
    }
    
    private Component createSummaryStats() {
        int totalPersons = personService.findAllPersons().size();
        int totalMeasurements = measurementService.findAllMeasurements().size();
        
        HorizontalLayout stats = new HorizontalLayout(
            createStat("Persons", String.valueOf(totalPersons)),
            createStat("Measurements", String.valueOf(totalMeasurements))
        );
        
        stats.addClassNames(
            LumoUtility.Padding.MEDIUM,
            LumoUtility.BoxShadow.SMALL,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Background.CONTRAST_5
        );
        
        return stats;
    }
    
    private Component createStat(String title, String value) {
        H3 titleH3 = new H3(title);
        titleH3.addClassNames(
            LumoUtility.FontWeight.NORMAL,
            LumoUtility.Margin.NONE
        );
        
        Span valueSpan = new Span(value);
        valueSpan.addClassNames(
            LumoUtility.FontSize.XXLARGE,
            LumoUtility.FontWeight.BOLD
        );
        
        VerticalLayout stat = new VerticalLayout(valueSpan, titleH3);
        stat.addClassNames(
            LumoUtility.Padding.Horizontal.LARGE,
            LumoUtility.Padding.Vertical.MEDIUM,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.TextAlignment.CENTER
        );
        stat.setSpacing(false);
        stat.setPadding(false);
        
        return stat;
    }
    
    private Component createRecentMeasurements() {
        H3 recentMeasurementsHeader = new H3("Recent Measurements");
        recentMeasurementsHeader.addClassNames(
            LumoUtility.Margin.Top.LARGE,
            LumoUtility.Margin.Bottom.MEDIUM
        );
        
        VerticalLayout layout = new VerticalLayout(recentMeasurementsHeader);
        layout.setSpacing(false);
        layout.setPadding(false);
        
        recentMeasurementsLayout = new VerticalLayout();
        recentMeasurementsLayout.setSpacing(false);
        recentMeasurementsLayout.setPadding(false);
        
        layout.add(recentMeasurementsLayout);
        
        updateRecentMeasurements();
        
        return layout;
    }
    
    private void updateRecentMeasurements() {
        recentMeasurementsLayout.removeAll();
        
        List<Measurement> recentMeasurements = measurementService.findRecentMeasurements(10);
        
        if (recentMeasurements.isEmpty()) {
            recentMeasurementsLayout.add(new Paragraph("No measurements recorded yet."));
        } else {
            for (Measurement measurement : recentMeasurements) {
                String personName = measurement.getPerson().getFirstName() + " " + measurement.getPerson().getLastName();
                recentMeasurementsLayout.add(createMeasurementCard(measurement, personName));
            }
        }
    }
    
    private Component createMeasurementCard(Measurement measurement, String personName) {
        String measurementType = measurement.getType().name();
        
        String formattedValue = String.format("%.2f", measurement.getMeasurementValue());
        String measurementValue = formattedValue + " " + measurement.getUnit();
        
        String measurementDate = measurement.getMeasuredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        
        Span personSpan = new Span(personName);
        personSpan.addClassNames(
            LumoUtility.FontWeight.BOLD
        );
        
        Span typeSpan = new Span(measurementType);
        typeSpan.getStyle().set("font-style", "italic");
        
        Span valueSpan = new Span(measurementValue);
        valueSpan.addClassNames(
            LumoUtility.FontWeight.BOLD
        );
        
        Span dateSpan = new Span(measurementDate);
        dateSpan.addClassNames(
            LumoUtility.TextColor.SECONDARY,
            LumoUtility.FontSize.SMALL
        );
        
        HorizontalLayout details = new HorizontalLayout(
            personSpan,
            new Span(":"),
            typeSpan,
            new Span("-"),
            valueSpan
        );
        details.setSpacing(true);
        
        VerticalLayout card = new VerticalLayout(details, dateSpan);
        card.addClassNames(
            LumoUtility.Padding.MEDIUM,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Background.CONTRAST_5,
            LumoUtility.BoxShadow.XSMALL,
            LumoUtility.Margin.Bottom.SMALL
        );
        card.setSpacing(false);
        
        return card;
    }
} 