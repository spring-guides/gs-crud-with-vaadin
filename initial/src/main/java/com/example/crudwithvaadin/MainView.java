package com.hello.crudwithvaadin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {

	public MainView() {
		add(new Button("Click me", e -> Notification.show("Hello, Spring+Vaadin user!")));
	}
}
