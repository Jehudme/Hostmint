package com.hostmint.app.web.vaadin.layouts;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@AnonymousAllowed
public class MainLayout extends VerticalLayout {

    public MainLayout() {
        getStyle().setHeight("100%");
    }
}
