package com.hostmint.app.web.vaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed; // <--- Use this import

@Route("console")
@AnonymousAllowed // <--- This allows anyone to see the dashboard without logging in
public class MainView extends VerticalLayout {

    public MainView() {
        add(new H1("HostMint Management Console"));

        Button btn = new Button("Test System", e -> {
            Notification.show("Vaadin is successfully connected to HostMint!");
        });

        add(btn);

        setAlignItems(Alignment.CENTER);
    }
}
