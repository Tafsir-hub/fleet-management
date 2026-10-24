package com.tafsir.fleetmanagement.ui.components;

import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Sidebar extends VBox {
	
	private static final String[] ITEMS =
		{"Vehicles", "Drivers", "Trips","Fuel", "Maintenance", "Reports", "Dashboard"};
	
	// "selected" is highlighted. "onNavigate" receives the name of the clicked item.
	public Sidebar(String selected, Consumer<String> onNavigate) {
		getStyleClass().add("sidebar");
		setPrefWidth(190);
		setMinWidth(190);
		
		for (String item: ITEMS) {
			getChildren().add(navButton(item, item.equals(selected), onNavigate));
		}
		
		Region spacer = new Region();
		VBox.setVgrow(spacer, Priority.ALWAYS); // Pushes Logout to the bottom
		Region line = new Region();
		line.getStyleClass().add("sidebar-line");
		getChildren().addAll(spacer,line,navButton("Logout", false, onNavigate));
	}

	private Button navButton(String text, boolean selected, Consumer<String> onNavigate) {
		Button button = new Button(text);
		button.setMaxWidth(Double.MAX_VALUE);
		button.getStyleClass().add("nav-button");
		if (selected) {
			button.getStyleClass().add("nav-button-selected");
		} else {
			button.setOnAction(e -> onNavigate.accept(text));
		}
		return button;
	}

}
