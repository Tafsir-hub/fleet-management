package com.tafsir.fleetmanagement.ui;


import java.sql.SQLException;

import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.ui.components.Dialogs;
import com.tafsir.fleetmanagement.ui.components.HeaderBar;
import com.tafsir.fleetmanagement.ui.components.TableColumns;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

public class VehicleScreen extends BorderPane {
	
	private final VehicleDAO vehicleDAO = new VehicleDAO();
	private final TableView<Vehicle> table = new TableView<>();
	
	public VehicleScreen() {
		setTop(new HeaderBar("FMAS	|	Vehicles"));
		
		table.getColumns().add(TableColumns.of("ID", "vehicleId", 60));
		table.getColumns().add(TableColumns.of("Make / Model", "makeModel", 240));
		table.getColumns().add(TableColumns.of("Year", "year", 70));
		table.getColumns().add(TableColumns.of("Type", "vehicleType", 130));
		table.getColumns().add(TableColumns.of("Mileage", "mileage", 100));
		table.getColumns().add(TableColumns.of("Status", "status", 110));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		table.setPlaceholder(new Label("No vehicles found"));
		
		setCenter(table);
		BorderPane.setMargin(table, new Insets(16));
		
		loadVehicles();
		
	}

	private void loadVehicles() {
		try {
			table.setItems(FXCollections.observableArrayList(vehicleDAO.findAll()));
		}catch (SQLException e) {
			Dialogs.error("Could not load vehicles", e.getMessage());
		}
		
	}

}
