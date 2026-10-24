package com.tafsir.fleetmanagement.ui;


import java.sql.SQLException;
import java.util.List;

import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;
import com.tafsir.fleetmanagement.ui.components.AllChoiceBox;
import com.tafsir.fleetmanagement.ui.components.Dialogs;
import com.tafsir.fleetmanagement.ui.components.TabSwitch;
import com.tafsir.fleetmanagement.ui.components.TableColumns;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;


public class VehicleScreen extends BorderPane {
	
	private static final String[] TYPES = 
		{"Van", "Pickup", "SUV", "Sports Car", "Box Truck", "Semi Tractor"};
	
	private static final AllChoiceBox<VehicleStatus> statusFilter = new AllChoiceBox<>(VehicleStatus.values());
	private static final AllChoiceBox<String> typeFilter = new AllChoiceBox<>(TYPES);
	private final VehicleDAO vehicleDAO = new VehicleDAO();
	private final TableView<Vehicle> table = new TableView<>();
	private final Label countLabel = new Label();
	private final BorderPane vehiclesPane = new BorderPane();
	private final StackPane driversPane = new StackPane();
	
	public VehicleScreen() {
		
		setPadding(new Insets(18, 20, 14, 0));
		TabSwitch tabs = new TabSwitch(this::showTab, "Vehicles", "Drivers");
		setTop(tabs);
		BorderPane.setMargin(tabs, new Insets(0, 0, 14, 0));
		
		buildVehiclesPane();
		buildDriversPane();
		showTab("Vehicles");
		
		statusFilter.setOnAction(e -> loadVehicles());
		typeFilter.setOnAction(e -> loadVehicles());
		loadVehicles();
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
		
		HBox filters = new HBox(10, new Label("Status:"), statusFilter, new Label("Type"), typeFilter);
		filters.setAlignment(Pos.CENTER_LEFT);
		filters.setPadding(new Insets(0, 0, 12, 0));
		
		table.getColumns().add(TableColumns.of("ID", "vehicleId", 60));
		table.getColumns().add(TableColumns.of("Make / Model", "makeModel", 240));
		table.getColumns().add(TableColumns.of("Year", "year", 70));
		table.getColumns().add(TableColumns.of("Type", "vehicleType", 130));
		table.getColumns().add(TableColumns.of("Mileage", "mileage", 100));
		table.getColumns().add(TableColumns.of("Status", "status", 110));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		table.setPlaceholder(new Label("No vehicles found"));
		
		countLabel.getStyleClass().add("muted");
		BorderPane.setMargin(countLabel, new Insets(8, 0, 0, 0));
		
		vehiclesPane.setTop(filters);
		vehiclesPane.setCenter(table);
		vehiclesPane.setBottom(countLabel);
		
	}
	
// Reads both filters, asks the DAO and shows the result in the table
	private void loadVehicles() {
		VehicleStatus status = statusFilter.getSelection();
		String type = typeFilter.getSelection();
		try {
			List<Vehicle> vehicles = vehicleDAO.findAll(status, type);
			table.setItems(FXCollections.observableArrayList(vehicles));
			countLabel.setText("Showing " + vehicles.size() + " vehicle(s)");
		}catch (SQLException e) {
			Dialogs.error("Could not load vehicles", e.getMessage());
		}
		
	}

}
