package com.tafsir.fleetmanagement.ui;



import java.sql.SQLException;
import java.util.List;

import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;
import com.tafsir.fleetmanagement.ui.components.AllChoiceBox;
import com.tafsir.fleetmanagement.ui.components.Dialogs;
import com.tafsir.fleetmanagement.ui.components.LabeledField;
import com.tafsir.fleetmanagement.ui.components.PrimaryButton;
import com.tafsir.fleetmanagement.ui.components.TabSwitch;
import com.tafsir.fleetmanagement.ui.components.TableColumns;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;


public class VehicleScreen extends BorderPane {
	
	private static final String[] TYPES = 
		{"Van", "Pickup", "SUV", "Sports Car", "Box Truck", "Semi Tractor"};
	
	private final AllChoiceBox<VehicleStatus> statusFilter = new AllChoiceBox<>(VehicleStatus.values());
	private final AllChoiceBox<String> typeFilter = new AllChoiceBox<>(TYPES);

	private final TextField searchField = new TextField();
	private final VehicleDAO vehicleDAO = new VehicleDAO();
	private final TableView<Vehicle> table = new TableView<>();
	private final Label countLabel = new Label();
	private final BorderPane vehiclesPane = new BorderPane();
	private final StackPane driversPane = new StackPane();
	private final ObservableList<Vehicle> loadedVehicles = FXCollections.observableArrayList();
	private final FilteredList<Vehicle> shownVehicles = new FilteredList<>(loadedVehicles, v -> true);
	
	
	public VehicleScreen() {
		
		setPadding(new Insets(18, 20, 14, 20));
		TabSwitch tabs = new TabSwitch(this::showTab, "Vehicles", "Drivers");
		setTop(tabs);
		BorderPane.setMargin(tabs, new Insets(0, 0, 16, 0));
		
		buildVehiclesPane();
		buildDriversPane();
		showTab("Vehicles");
		
		searchField.textProperty().addListener((obs, oldText, newText) -> applySearch());
		statusFilter.setOnAction(e -> loadVehicles());
		typeFilter.setOnAction(e -> loadVehicles());
		loadVehicles();
	}
	
	private void applySearch() {
		String text = searchField.getText() == null ? "": searchField.getText().trim().toLowerCase();
		shownVehicles.setPredicate(v -> text.isEmpty() || v.getMakeModel().toLowerCase().contains(text));
		countLabel.setText("Showing " + shownVehicles.size() + " vehicle(s)");
		
	}

	private void showTab(String tab) {
		setCenter("Drivers".equals(tab) ? driversPane: vehiclesPane);
		
	}

	private void buildDriversPane() {
		Label note = new Label("Drivers arrive in Slice 3");
		note.getStyleClass().add("muted");
		driversPane.getChildren().add(note);
		
	}

	private void buildVehiclesPane() {
		
		searchField.setPromptText("Search by make/model...");
		searchField.setMaxWidth(Double.MAX_VALUE);
		statusFilter.setPrefWidth(170);
		typeFilter.setPrefWidth(170);
		
		LabeledField searchBox = new LabeledField("Search", searchField);
		HBox.setHgrow(searchBox, Priority.ALWAYS);
		
		PrimaryButton addButton = new PrimaryButton("+ Add Vehicle");
		addButton.setPrefWidth(200);
		addButton.setOnAction(e ->
				Dialogs.info("Coming soon", "The Add Vehicle form is built in the next steps."));
		
		HBox filters = new HBox(16, searchBox,
				new LabeledField("Status", statusFilter),
				new LabeledField("Type", typeFilter),
				addButton);
		filters.setAlignment(Pos.BOTTOM_LEFT);
		filters.setPadding(new Insets(0, 0, 14, 0));
		
		table.getColumns().add(TableColumns.<Vehicle>formatted("ID", v -> String.format("V-%03d", v.getVehicleId()), 90));
        table.getColumns().add(TableColumns.of("Make / Model", "makeModel", 260));
        table.getColumns().add(TableColumns.of("Year", "year", 80));
        table.getColumns().add(TableColumns.of("Type", "vehicleType", 140));
        table.getColumns().add(TableColumns.<Vehicle>formatted("Mileage", v -> String.format("%,d", v.getMileage()), 120));
        table.getColumns().add(TableColumns.of("Status", "status", 120));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No vehicles match these filters"));
        table.setItems(shownVehicles);
		
		countLabel.getStyleClass().add("muted");
		BorderPane.setMargin(countLabel, new Insets(8, 0, 0, 0));
		
		vehiclesPane.setTop(filters);
		vehiclesPane.setCenter(table);
		vehiclesPane.setBottom(countLabel);
		
	}
	
// Reads both filters, asks the DAO and shows the result in the table
	private void loadVehicles() {
		
		try {
			List<Vehicle> vehicles = vehicleDAO.findAll(statusFilter.getSelection(), typeFilter.getSelection());
			loadedVehicles.setAll(vehicles);
			applySearch();
		}catch (SQLException e) {
			Dialogs.error("Could not load vehicles", e.getMessage());
		}
		
	}

}
