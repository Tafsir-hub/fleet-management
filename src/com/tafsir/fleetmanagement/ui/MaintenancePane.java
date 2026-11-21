package com.tafsir.fleetmanagement.ui;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.dao.MaintenanceDAO;
import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.model.MaintenanceRecord;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.ui.components.TableColumns;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

// The maintenance tab or the Fuel & Maintenance screen
public class MaintenancePane extends BorderPane {
	
	private static final DateTimeFormatter DATE_FMT = 
			DateTimeFormatter.ofPattern("MM/dd/yyyy");
	
	private final MaintenanceDAO maintenanceDAO = new MaintenanceDAO();
	private final VehicleDAO vehicleDAO = new VehicleDAO();
	
	// History
	private final ComboBox<String> vehicleFilter = new ComboBox<>();
	private final TableView<MaintenanceRecord> table = new TableView<>();
	private final Label historyTitle = new Label();
	private final Label countLabel = new Label();
	private final MessageBar messageBar = new MessageBar();
	
	private List<Vehicle> vehicles = new ArrayList<>();
	
	public MaintenancePane() {
		setCenter(buildHistory());
		
		loadVehicles();
		vehicleFilter.valueProperty().addListener((obs, old, v) -> loadRecords());
		loadRecords();
	}

	private VBox buildHistory() {
		historyTitle.getStyleClass().add("section-title");
		vehicleFilter.setPrefWidth(320);
		
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		HBox header = new HBox(12, historyTitle, spacer, new Label("Vehicle"), vehicleFilter);
		header.setAlignment(Pos.CENTER_LEFT);	
		
		table.getColumns().add(TableColumns.<MaintenanceRecord> formatted(
				"Date", r -> r.getServiceDate().format(DATE_FMT), 110));
		table.getColumns().add(TableColumns.<MaintenanceRecord> formatted(
				"Vehicle", r -> vehicleLabel(r.getVehicleId()), 280));
		table.getColumns().add(TableColumns.<MaintenanceRecord> formatted(
				"Service", r -> r.getServiceType(), 280));
		table.getColumns().add(TableColumns.<MaintenanceRecord> formatted(
				"Cost", r -> String.format("$%,.2f", r.getCost()), 110));
		table.getColumns().add(TableColumns.<MaintenanceRecord> formatted(
				"Mileage", r -> String.format("%,d", r.getMileage()), 110));
		
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		
		table.setPlaceholder(new Label("No maintenance record for this"));
		
		countLabel.getStyleClass().add("muted");
		VBox footer =  new VBox(8, messageBar, countLabel);
		footer.setPadding(new Insets(8, 0, 0, 0));
		
		VBox history = new VBox(8, header, table, footer);
		VBox.setVgrow(table, Priority.ALWAYS);
		return history;
			
	}
	
	// ---------------Loading------------------
	
	// The history filter lists every vehicle: "All vehicles" first
	private void loadVehicles() {
		try {
			vehicles = vehicleDAO.findAll(null, null);
		} catch (SQLException e) {
			e.printStackTrace();
			vehicles =new ArrayList<>();
		}
		List<String> labels = new ArrayList<>();
		labels.add("All vehicles");
		for (Vehicle v: vehicles)
			labels.add(vehicleLabel(v.getVehicleId()));
		vehicleFilter.setItems(FXCollections.observableArrayList(labels));
		vehicleFilter.getSelectionModel().selectFirst();
	}
	
	private void loadRecords() {
		int idx = vehicleFilter.getSelectionModel().getSelectedIndex();
		if (idx < 0) return;
		try {
			List<MaintenanceRecord> records;
			if (idx == 0) {
				records =maintenanceDAO.findAll();
				historyTitle.setText("MAINTENANCE HISTORY — All vehicles");
			} else {
				int vehicleId = vehicles.get(idx - 1).getVehicleId();
				records = maintenanceDAO.findByVehicle(vehicleId);
				historyTitle.setText("MAINTENANCE HISTORY — " + vehicleLabel(vehicleId));
			}
			table.setItems(FXCollections.observableArrayList(records));
			countLabel.setText("Showing " + records.size() + " maintenance record(s)");
		} catch (SQLException e) {
			e.printStackTrace();
			countLabel.setText("Could not load maintenance records");
		}
	}
	
	//-----------------Helpers-----------------
	private String vehicleLabel(int vehicleId) {
		for (Vehicle v: vehicles) {
			if (v.getVehicleId() == vehicleId)
				return v.getMakeModel() + " (" + String.format("V-%03d",  vehicleId) + ")";
		}
		return String.format("V-%03d", vehicleId);
	}

}
