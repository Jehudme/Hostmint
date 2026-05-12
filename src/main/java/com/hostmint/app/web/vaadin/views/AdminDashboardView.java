package com.hostmint.app.web.vaadin.views;

import com.hostmint.app.security.AuthoritiesConstants;
import com.hostmint.app.service.AuditLogService;
import com.hostmint.app.service.ProjectService;
import com.hostmint.app.service.dto.AuditLogDTO;
import com.hostmint.app.service.dto.ProjectDTO;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Route("admin/dashboard")
@PageTitle("Admin Dashboard | HostMint")
@RolesAllowed(AuthoritiesConstants.ADMIN)
public class AdminDashboardView extends VerticalLayout {

    private final AuditLogService auditLogService;
    private final ProjectService projectService;

    public AdminDashboardView(AuditLogService auditLogService, ProjectService projectService) {
        this.auditLogService = auditLogService;
        this.projectService = projectService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);
        addClassNames(LumoUtility.Background.CONTRAST_5);

        add(createTopNavBar());

        VerticalLayout content = new VerticalLayout();
        content.addClassNames(LumoUtility.Padding.LARGE, LumoUtility.Gap.LARGE);
        content.setSizeFull();

        content.add(createStatsBar());
        content.add(createAuditSection());
        content.add(createProjectSection());

        add(content);
        expand(content);
    }

    private HorizontalLayout createTopNavBar() {
        HorizontalLayout nav = new HorizontalLayout();
        nav.setWidthFull();
        nav.addClassNames(
            LumoUtility.Background.BASE,
            LumoUtility.Padding.Horizontal.MEDIUM,
            LumoUtility.BoxShadow.SMALL,
            LumoUtility.AlignItems.CENTER
        );
        nav.setHeight("64px");

        H2 logo = new H2("HostMint Admin");
        logo.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE, LumoUtility.TextColor.PRIMARY);

        Button logout = new Button("Sign Out", VaadinIcon.SIGN_OUT.create(), e -> UI.getCurrent().navigate("login"));
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);

        nav.add(logo);
        nav.setFlexGrow(1, logo);
        nav.add(logout);
        return nav;
    }

    private HorizontalLayout createStatsBar() {
        HorizontalLayout stats = new HorizontalLayout();
        stats.setWidthFull();
        stats.setSpacing(true);

        stats.add(createStatCard("Total Nodes", "124", VaadinIcon.SERVER));
        stats.add(createStatCard("Active Users", "89", VaadinIcon.USERS));
        stats.add(createStatCard("System Uptime", "99.9%", VaadinIcon.CHART));
        stats.add(createStatCard("Security Alerts", "0", VaadinIcon.SHIELD));

        return stats;
    }

    private VerticalLayout createStatCard(String title, String value, VaadinIcon icon) {
        VerticalLayout card = new VerticalLayout();
        card.addClassNames(
            LumoUtility.Background.BASE,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Padding.MEDIUM,
            LumoUtility.BoxShadow.SMALL
        );
        card.setSpacing(false);
        card.setAlignItems(Alignment.CENTER);

        Span titleSpan = new Span(title);
        titleSpan.addClassNames(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.SECONDARY, LumoUtility.FontWeight.BOLD);

        Span valSpan = new Span(value);
        valSpan.addClassNames(LumoUtility.FontSize.XXLARGE, LumoUtility.FontWeight.BLACK);

        card.add(icon.create(), valSpan, titleSpan);
        return card;
    }

    private VerticalLayout createAuditSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassNames(
            LumoUtility.Background.BASE,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Padding.MEDIUM,
            LumoUtility.BoxShadow.SMALL
        );

        H3 title = new H3("Immutable Audit History");
        title.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.FontSize.MEDIUM);

        Grid<AuditLogDTO> auditGrid = new Grid<>(AuditLogDTO.class, false);
        auditGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);

        auditGrid.addColumn(AuditLogDTO::getCreatedAt).setHeader("Timestamp").setSortable(true).setAutoWidth(true);
        auditGrid.addColumn(AuditLogDTO::getPrincipal).setHeader("User").setAutoWidth(true);
        auditGrid.addColumn(AuditLogDTO::getAction).setHeader("Action").setAutoWidth(true);
        auditGrid.addColumn(AuditLogDTO::getMessage).setHeader("Event Summary");

        // CORRECTED: Using the eager relationship method from your service
        auditGrid.setItems(query ->
            auditLogService
                .findAllWithEagerRelationships(PageRequest.of(query.getPage(), query.getPageSize(), Sort.by("createdAt").descending()))
                .stream()
        );

        auditGrid.setAllRowsVisible(true);
        section.add(title, auditGrid);
        return section;
    }

    private VerticalLayout createProjectSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassNames(
            LumoUtility.Background.BASE,
            LumoUtility.BorderRadius.MEDIUM,
            LumoUtility.Padding.MEDIUM,
            LumoUtility.BoxShadow.SMALL
        );

        H3 title = new H3("Global Node Infrastructure");
        title.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.FontSize.MEDIUM);

        Grid<ProjectDTO> projectGrid = new Grid<>(ProjectDTO.class, false);
        projectGrid.addThemeVariants(GridVariant.LUMO_COMPACT);

        projectGrid.addColumn(ProjectDTO::getName).setHeader("Node Name").setSortable(true);
        projectGrid.addColumn(p -> "Live").setHeader("Status");

        projectGrid
            .addComponentColumn(p -> {
                Button manage = new Button("Manage", VaadinIcon.COG.create());
                manage.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
                return manage;
            })
            .setHeader("Actions");

        // CORRECTED: Using the eager relationship method from your service
        projectGrid.setItems(query ->
            projectService.findAllWithEagerRelationships(PageRequest.of(query.getPage(), query.getPageSize())).stream()
        );

        projectGrid.setAllRowsVisible(true);
        section.add(title, projectGrid);
        return section;
    }
}
