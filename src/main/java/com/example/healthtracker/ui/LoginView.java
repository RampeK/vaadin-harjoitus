package com.example.healthtracker.ui;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route("/login")
@RouteAlias("/")
@PageTitle("Login | Health Tracker")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        addClassName("login-view");
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        login.setAction("login");
        login.setForgotPasswordButtonVisible(false);
        
        LoginI18n i18n = LoginI18n.createDefault();
        i18n.getForm().setTitle("Health Tracker Login");
        i18n.getForm().setUsername("Username");
        i18n.getForm().setPassword("Password");
        i18n.getForm().setSubmit("Log in");
        i18n.getErrorMessage().setTitle("Incorrect username or password");
        i18n.getErrorMessage().setMessage("Check that you have entered the correct username and password and try again.");
        login.setI18n(i18n);

        // Create header with logo and title
        H1 header = new H1("Health Tracker");
        header.addClassNames(
            LumoUtility.FontSize.XXLARGE,
            LumoUtility.Margin.Bottom.MEDIUM
        );
        
        Paragraph description = new Paragraph("Log in to manage your health measurements");
        description.addClassNames(
            LumoUtility.Margin.Bottom.LARGE,
            LumoUtility.TextColor.SECONDARY
        );
        
        Paragraph note = new Paragraph("Use username: user and password: password to log in");
        note.addClassNames(
            LumoUtility.FontSize.SMALL,
            LumoUtility.TextColor.TERTIARY,
            LumoUtility.Margin.Top.MEDIUM
        );
        
        add(
            header,
            description,
            login,
            note
        );
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Check if the user is already authenticated
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null 
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated()) {
            event.forwardTo(DashboardView.class);
        }
        
        // Check for login error
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
} 