package com.tafsir.fleetmanagement.ui;

import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MainMenuScreen extends VBox {
	
	private static final String[] QUICK_ACTIONS = {"Add Vehicle", "Log Trip", "Record Fuel", "Maintenance"};
	
	public MainMenuScreen(Consumer<String> onQuickAction) {
		super(18);
		setPadding(new Insets(24, 28, 20, 28));
		
		Label welcome = new Label("Welcome back, Fleet Manager");
		welcome.getStyleClass().add("menu-welcome");
		
		HBox stats = new HBox (20, statCard("Active Vehicles"), statCard("Due for Maintenance"), statCard("Operating Cost (MTD)"));
		
		HBox actions = new HBox(16);
		for (String action : QUICK_ACTIONS) {
			Button button = new Button("+ " + action);
			button.getStyleClass().add("quick-action");
			button.setMaxWidth(Double.MAX_VALUE);
			button.setMinWidth(0);
			button.setPrefWidth(100);
			HBox.setHgrow(button, Priority.ALWAYS);
			button.setOnAction(e -> onQuickAction.accept(action));
			actions.getChildren().add(button);
		}
		
		Label attentionNote = new Label("Appears once the health score is built (slice 9).");
		attentionNote.getStyleClass().add("muted");
		
		getChildren().addAll(welcome, stats, caption("QUICK ACTIONS"), actions,
				caption("VEHICLES NEEDING ATTENTION"), attentionNote); 
	}
	
	private VBox statCard(String label) {
		Label value = new Label("\u2014");
		value.getStyleClass().add("stat-value");
		Label name = new Label(label);
		name.getStyleClass().add("stat-label");
		VBox card = new VBox(4, value, name);
		card.getStyleClass().add("stat-card");
		card.setMaxWidth(Double.MAX_VALUE);
		card.setMinWidth(0);
		card.setPrefWidth(100);
		HBox.setHgrow(card, Priority.ALWAYS);
		return card;
	}
	
	private Label caption(String text){
		Label label = new Label(text);
		label.getStyleClass().add("section-caption");
		return label;
		
	}

}
