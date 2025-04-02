package com.example.healthtracker.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.Component;
import org.springframework.security.core.context.SecurityContextHolder;
import com.vaadin.flow.server.VaadinServletRequest;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

public class MainLayout extends AppLayout {

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        // Create app header with logo and title
        H1 logo = new H1("Health Tracker");
        logo.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM);
        
        // App icon
        Icon healthIcon = VaadinIcon.HEART.create();
        healthIcon.setColor("#FF5252");
        healthIcon.setSize("24px");

        // Get current user info
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        
        // User avatar with role indicator
        Avatar avatar = new Avatar(username);
        avatar.setColorIndex(isAdmin ? 2 : 1); // Different color for admin
        avatar.getElement().setAttribute("tabindex", "-1");
        
        // Logout button
        Button logoutButton = new Button("Logout", VaadinIcon.SIGN_OUT.create());
        logoutButton.addClickListener(e -> logout());

        // Layout for the header components
        HorizontalLayout header;
        
        if (isAdmin) {
            Span adminBadge = new Span("Admin");
            adminBadge.getElement().getThemeList().add("badge success");
            HorizontalLayout userLayout = new HorizontalLayout(new Span(username), adminBadge);
            userLayout.setSpacing(true);
            userLayout.setPadding(false);
            userLayout.setAlignItems(FlexComponent.Alignment.CENTER);
            
            header = new HorizontalLayout(
                new DrawerToggle(),
                healthIcon,
                logo,
                avatar,
                userLayout,
                logoutButton
            );
        } else {
            Span userInfo = new Span(username);
            header = new HorizontalLayout(
                new DrawerToggle(),
                healthIcon,
                logo,
                avatar,
                userInfo,
                logoutButton
            );
        }

        // Configure header layout
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Background.BASE,
                LumoUtility.BoxShadow.SMALL);

        addToNavbar(header);
    }

    private void logout() {
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(
                VaadinServletRequest.getCurrent().getHttpServletRequest(), null,
                null);
        
        // Navigate to login page using UI.navigate()
        getUI().ifPresent(ui -> ui.navigate("/login"));
    }

    private void createDrawer() {
        // Create menu items with icons for better visual hierarchy
        Nav menu = new Nav();
        menu.addClassNames(LumoUtility.Padding.MEDIUM);

        // Create menu items
        VerticalLayout menuLayout = new VerticalLayout();
        menuLayout.setPadding(false);
        menuLayout.setSpacing(true);
        
        // Dashboard
        menuLayout.add(createMenuLink(VaadinIcon.DASHBOARD, "Dashboard", DashboardView.class));
        
        // Persons
        menuLayout.add(createMenuLink(VaadinIcon.USERS, "Persons", PersonsView.class));
        
        // Measurements
        menuLayout.add(createMenuLink(VaadinIcon.CHART, "Measurements", MeasurementsView.class));
        
        // Health Goals
        menuLayout.add(createMenuLink(VaadinIcon.FLAG_O, "Health Goals", GoalsView.class));
        
        // Medical Notes
        menuLayout.add(createMenuLink(VaadinIcon.FILE_TEXT, "Medical Notes", MedicalNotesView.class));
        
        menu.add(menuLayout);
        
        addToDrawer(menu);
    }
    
    private <T extends Component> RouterLink createMenuLink(VaadinIcon icon, String text, Class<T> viewClass) {
        // Create icon
        Icon vaadinIcon = icon.create();
        vaadinIcon.setSize("16px");
        
        // Create text
        Span textSpan = new Span(text);
        textSpan.addClassNames(LumoUtility.FontWeight.MEDIUM);
        
        // Create layout
        HorizontalLayout linkLayout = new HorizontalLayout(vaadinIcon, textSpan);
        linkLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        linkLayout.setSpacing(true);
        
        // Create RouterLink using the view class directly as recommended in docs
        RouterLink link = new RouterLink();
        link.setRoute(viewClass);
        link.getElement().getChildren().forEach(child -> child.removeFromParent());
        link.add(linkLayout);
        
        // Add styling
        link.addClassNames(
                LumoUtility.Display.FLEX,
                LumoUtility.Width.FULL,
                LumoUtility.Padding.SMALL,
                LumoUtility.BorderRadius.MEDIUM);
        
        // Add hover effect
        link.getElement().addEventListener("mouseover", 
                e -> link.addClassName(LumoUtility.Background.CONTRAST_5))
                .addEventData("event.target.className");
        
        link.getElement().addEventListener("mouseout", 
                e -> link.removeClassName(LumoUtility.Background.CONTRAST_5))
                .addEventData("event.target.className");
        
        return link;
    }
} 