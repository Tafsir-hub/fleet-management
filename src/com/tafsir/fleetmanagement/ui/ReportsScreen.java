package com.tafsir.fleetmanagement.ui;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

//>> The Reports screen: a row of report tiles on top, the chosen report below.
//>> Each report is its own pane, so adding a report means adding a tile and a case in showReport.
public class ReportsScreen extends BorderPane {

	private static final String COST = "Operating Cost";
	private static final String MPG = "Fuel Efficiency";
	private static final String DUE = "Maintenance Due";

	private final ToggleGroup group = new ToggleGroup();

	public ReportsScreen() {
		setTop(buildPicker());
		showReport(COST);
	}

	//---------Picker---------------

	private VBox buildPicker() {
		Label caption = new Label("CHOOSE A REPORT");
		caption.getStyleClass().add("section-title");

		HBox tiles = new HBox(16,
				tile(COST, "Fuel plus maintenance cost per vehicle"),
				tile(MPG, "Miles per gallon, from trips and fuel"),
				tile(DUE, "Vehicles that are due for service"));

		group.getToggles().get(0).setSelected(true);
		group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
			if (newToggle == null) {
				oldToggle.setSelected(true); // clicking the chosen tile again keeps it chosen
			} else {
				showReport((String) newToggle.getUserData());
			}
		});

		VBox picker = new VBox(12, caption, tiles);
		picker.setPadding(new Insets(24, 24, 0, 24));
		return picker;
	}

	// One tile: a bold name over a short hint. The name is kept in userData to know which report to show.
	private ToggleButton tile(String name, String hint) {
		Label title = new Label(name);
		title.getStyleClass().add("report-tile-title");
		Label sub = new Label(hint);
		sub.getStyleClass().add("report-tile-hint");

		ToggleButton button = new ToggleButton();
		button.setGraphic(new VBox(4, title, sub));
		button.setUserData(name);
		button.setToggleGroup(group);
		button.getStyleClass().add("report-tile");
		button.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(button, Priority.ALWAYS);
		return button;
	}

	//---------Reports---------------

	private void showReport(String name) {
		Node pane;
		switch (name) {
		case MPG:
			pane = new FuelEfficiencyPane();
			break;
		case DUE:
			pane = placeholder(DUE);
			break;
		default:
			pane = new CostReportPane();
		}
		setCenter(pane);
	}

	// Stands in for a report that is not built yet
	private VBox placeholder(String name) {
		Label title = new Label(name.toUpperCase() + " REPORT");
		title.getStyleClass().add("section-title");
		Label note = new Label("This report is added in the next step.");
		note.getStyleClass().add("muted");
		VBox box = new VBox(8, title, note);
		box.setPadding(new Insets(24));
		return box;
	}
}
