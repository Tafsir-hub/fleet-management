package com.tafsir.fleetmanagement.ui.components;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {
	
	private static final String[] ITEMS =
		{"Main Menu", "Vehicles", "Drivers", "Trips","Fuel", "Maintenance", "Reports", "Dashboard"};
	
	private final Map<String, Button> buttons = new HashMap<>();
	private String current;
	
	// "selected" is highlighted. "onNavigate" receives the name of the clicked item.
	public Sidebar(String selected, Consumer<String> onNavigate) {
		getStyleClass().add("sidebar");
		setPrefWidth(190);
		setMinWidth(190);
		
		for (String item: ITEMS) {
			getChildren().add(navButton(item, onNavigate));
		}
		select(selected);
		
		Region spacer = new Region();
		VBox.setVgrow(spacer, Priority.ALWAYS); // Pushes Logout to the bottom
		Region line = new Region();
		line.getStyleClass().add("sidebar-line");
		getChildren().addAll(spacer,line,navButton("Logout", onNavigate));
	}

	private Button navButton(String text, Consumer<String> onNavigate) {
		Button button = new Button(text);
		button.setMaxWidth(Double.MAX_VALUE);
		button.getStyleClass().add("nav-button");
		// Clicking the item that is already highlighted does nothing
		button.setOnAction(e -> {
			if (!text.equals(current)) {
				onNavigate.accept(text);
			}
		});
		buttons.put(text, button);
		return button;
	}
	
	// Moves the highlight to another item (used when a tab inside a screen changes)
	public void select(String name) {
		Button old = buttons.get(current);
		if (old != null) {
			old.getStyleClass().remove("nav-button-selected");
		}
		Button next = buttons.get(name);
		if (next != null) {
			next.getStyleClass().add("nav-button-selected");
			current = name;
		}
	}

}