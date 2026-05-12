package com.hostmint.app.web.vaadin.views;

import com.hostmint.app.security.AuthoritiesConstants;
import com.vaadin.flow.component.card.Card;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Route(value = "login")
@PageTitle("Login | HostMint")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterListener {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        // 1. SELF-CONTAINED CENTERING
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        // Dark themed background applied directly to the view
        addClassNames(LumoUtility.Background.CONTRAST_5);

        // 2. THE CARD
        Card card = new Card();
        card.addClassNames(LumoUtility.BoxShadow.XLARGE, LumoUtility.Background.BASE);
        card.setWidth("420px");

        // Header Image
        Image logoImage = new Image("https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800", "HostMint Background");
        logoImage.addClassNames(LumoUtility.BorderRadius.MEDIUM);
        logoImage.setHeight("160px");
        logoImage.setWidthFull();
        logoImage.getStyle().set("object-fit", "cover");
        card.setMedia(logoImage);

        // Content Container
        VerticalLayout cardContent = new VerticalLayout();
        cardContent.addClassNames(LumoUtility.Padding.LARGE, LumoUtility.Gap.SMALL);
        cardContent.setAlignItems(Alignment.CENTER);

        H1 title = new H1("HostMint");
        title.addClassNames(LumoUtility.FontSize.XXLARGE, LumoUtility.Margin.NONE, LumoUtility.TextColor.PRIMARY);

        Span subtitle = new Span("Access your cloud infrastructure");
        subtitle.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        // Login Logic
        login.setAction("login");
        login.setForgotPasswordButtonVisible(true);
        login.getStyle().set("padding", "0");

        // Footer Link
        RouterLink signUpLink = new RouterLink("New to the network? Join here", RegisterView.class);
        signUpLink.addClassNames(LumoUtility.Margin.Top.MEDIUM, LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY);

        cardContent.add(title, subtitle, login, signUpLink);
        card.add(cardContent);

        add(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // FIX: Check if the user is already authenticated
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            // Determine if the user is an Admin to redirect to the correct dashboard
            boolean isAdmin = auth
                .getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals(AuthoritiesConstants.ADMIN));

            if (isAdmin) {
                event.forwardTo(AdminDashboardView.class);
            } else {
                // Redirect to Client Dashboard when implemented, or a common landing page
                event.forwardTo(AdminDashboardView.class);
            }
        }

        // Handle login errors (redirected from Spring Security with ?error)
        if (event.getLocation().getQueryParameters().getParameters().containsKey("error")) {
            login.setError(true);
        }
    }
}
