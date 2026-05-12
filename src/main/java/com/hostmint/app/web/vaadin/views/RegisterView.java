package com.hostmint.app.web.vaadin.views;

import com.hostmint.app.service.UserService;
import com.hostmint.app.service.dto.AdminUserDTO;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("signup")
@PageTitle("Register | HostMint")
@AnonymousAllowed
public class RegisterView extends VerticalLayout {

    private final UserService userService;

    public RegisterView(UserService userService) {
        this.userService = userService;

        // 1. FULL PAGE CENTERING
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        // Deep charcoal/dark background for the full page
        addClassNames(LumoUtility.Background.CONTRAST_5);

        // 2. THE CARD (Slightly wider than Login to accommodate the FormLayout)
        Card card = new Card();
        card.addClassNames(LumoUtility.BoxShadow.XLARGE, LumoUtility.Background.BASE);
        card.setWidth("550px");

        // Header Image (Matching the Login style)
        Image headerImage = new Image("https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800", "HostMint Network");
        headerImage.addClassNames(LumoUtility.BorderRadius.MEDIUM);
        headerImage.setHeight("140px");
        headerImage.setWidthFull();
        headerImage.getStyle().set("object-fit", "cover");
        card.setMedia(headerImage);

        // 3. INTERNAL CARD CONTENT
        VerticalLayout cardContent = new VerticalLayout();
        cardContent.addClassNames(LumoUtility.Padding.LARGE, LumoUtility.Gap.SMALL);

        H1 title = new H1("Join the Network");
        title.addClassNames(LumoUtility.FontSize.XXLARGE, LumoUtility.Margin.NONE, LumoUtility.TextColor.PRIMARY);

        Span subtitle = new Span("Register your hosting capacity today");
        subtitle.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.Margin.Bottom.MEDIUM);

        // Registration Fields
        TextField login = new TextField("Username");
        TextField firstName = new TextField("First Name");
        TextField lastName = new TextField("Last Name");
        EmailField email = new EmailField("Email");
        PasswordField password = new PasswordField("Password");
        PasswordField confirmPassword = new PasswordField("Confirm Password");

        // Action Button
        Button registerButton = new Button("Create Account", e -> {
            if (!password.getValue().equals(confirmPassword.getValue())) {
                Notification.show("Error: Passwords do not match").addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                // Populate the DTO
                AdminUserDTO userDTO = new AdminUserDTO();
                userDTO.setLogin(login.getValue());
                userDTO.setFirstName(firstName.getValue());
                userDTO.setLastName(lastName.getValue());
                userDTO.setEmail(email.getValue());

                // Triggers the @Auditable logic in PrimaryUserService
                userService.registerUser(userDTO, password.getValue());

                Notification.show("Registration successful! Please sign in.").addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                UI.getCurrent().navigate(LoginView.class);
            } catch (Exception ex) {
                Notification.show("Registration failed: " + ex.getMessage()).addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.setWidthFull();
        registerButton.addClassNames(LumoUtility.Margin.Top.MEDIUM);

        // Footer Navigation
        RouterLink loginLink = new RouterLink("Already have an account? Sign in", LoginView.class);
        loginLink.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);

        // Form Layout Configuration
        FormLayout formLayout = new FormLayout(login, firstName, lastName, email, password, confirmPassword);
        formLayout.setResponsiveSteps(new FormLayout.ResponsiveStep("0", 1), new FormLayout.ResponsiveStep("300px", 2));
        formLayout.setColspan(login, 2);
        formLayout.setColspan(email, 2);
        formLayout.setWidthFull();

        // Assemble Component Tree
        cardContent.add(title, subtitle, formLayout, registerButton, loginLink);
        cardContent.setAlignItems(Alignment.CENTER);
        card.add(cardContent);

        add(card);
    }
}
