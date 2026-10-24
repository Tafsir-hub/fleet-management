package com.tafsir.fleetmanagement.ui;


import java.sql.SQLException;
import java.util.List;

import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;
import com.tafsir.fleetmanagement.ui.components.AllChoiceBox;
import com.tafsir.fleetmanagement.ui.components.Dialogs;
import com.tafsir.fleetmanagement.ui.components.HeaderBar;
import com.tafsir.fleetmanagement.ui.components.TableColumns;
import com.tafsir.fleetmanagement.ui.components.Theme;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class VehicleScreen extends BorderPane {
	
	private static final String[] TYPES = 
		{"Van", "Pickup", "SUV", "Sports Car", "Box Truck", "Semi Tractor"};
	
	private static final AllChoiceBox<VehicleStatus> statusFilter = new AllChoiceBox<>(VehicleStatus.values());
	private static final AllChoiceBox<String> typeFilter = new AllChoiceBox<>(TYPES);
	private final VehicleDAO vehicleDAO = new VehicleDAO();
	private final TableView<Vehicle> table = new TableView<>();
	private final Label countLabel = new Label();
	
	public VehicleScreen() {
		setTop(new VBox(new HeaderBar("FMAS	|	Vehicles"), buildFilterBar()));
		
		table.getColumns().add(TableColumns.of("ID", "vehicleId", 60));
		table.getColumns().add(TableColumns.of("Make / Model", "makeModel", 240));
		table.getColumns().add(TableColumns.of("Year", "year", 70));
		table.getColumns().add(TableColumns.of("Type", "vehicleType", 130));
		table.getColumns().add(TableColumns.of("Mileage", "mileage", 100));
		table.getColumns().add(TableColumns.of("Status", "status", 110));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		table.setPlaceholder(new Label("No vehicles found"));
		
		setCenter(table);
		BorderPane.setMargin(table, new Insets(0, 16, 0, 16));
		
		countLabel.setStyle("-fx-text-fill: " + Theme.MUTED + ";");
		setBottom(countLabel);
		BorderPane.setMargin(countLabel, new Insets(8, 16, 12, 16));
		
		statusFilter.setOnAction(e -> loadVehicles());
		typeFilter.setOnAction(e -> loadVehicles());
		
		loadVehicles();
		
	}
	
	private HBox buildFilterBar() {
		HBox bar = new HBox(10, new Label("Status:"), statusFilter, new Label("Type"), typeFilter);
		bar.setAlignment(Pos.CENTER_LEFT);
		bar.setPadding(new Insets(14, 16, 14, 16));
		return bar;
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
