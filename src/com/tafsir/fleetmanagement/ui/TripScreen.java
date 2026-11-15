package com.tafsir.fleetmanagement.ui;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.tafsir.fleetmanagement.dao.TripDAO;
import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.model.Trip;
import com.tafsir.fleetmanagement.model.Vehicle;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class TripScreen extends BorderPane {
	
	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyy");
	
	private final VehicleDAO vehicleDAO = new VehicleDAO();
	private final TripDAO tripDAO = new TripDAO();
	
	private final ComboBox<Vehicle> vehicleBox = new ComboBox<>();
	private final Label historyTitle = new Label("TRIP HISTORY");
	private final TableView<Trip> table = new TableView<>();
	private final Label countLabel = new Label();
	
	public TripScreen() {
		setPadding(new Insets(24));
		
		buildVehicleBox();
		buildTable();
		
		Label pickLabel = new Label("VEHICLE");
		VBox top = new VBox(6, pickLabel, vehicleBox);
		top.setPadding(new Insets(0, 0, 20, 0));
		
		VBox center = new VBox(8, historyTitle, table);
		VBox.setVgrow(table, javafx.scene.layout.Priority.ALWAYS);
		
		countLabel.getStyleClass().add("muted");
		
		setTop(top);
		setCenter(center);
		setBottom(countLabel);
		
		loadVehicles();
	}
	private void buildVehicleBox() {
		vehicleBox.setPrefWidth(320);
		vehicleBox.setConverter(new StringConverter<Vehicle>() {
			@Override public String toString(Vehicle v) {
				return v == null ? "" : v.getMakeModel() + " (" + displayId(v) +")";
				
			}
			@Override public Vehicle fromString(String s) {
				return null;
			}
		});	
		vehicleBox.valueProperty().addListener((obs, old, v) -> loadHistory());
		}
	
	private void buildTable() {
		TableColumn<Trip, String> dateCol = new TableColumn<>("Date");
		dateCol.setCellValueFactory(c ->
				new javafx.beans.property.SimpleStringProperty(c.getValue().getTripDate().format(DATE_FMT)));
		
		TableColumn<Trip, String> driverCol = new TableColumn<>("Driver");
		driverCol.setCellValueFactory(c -> 
				new javafx.beans.property.SimpleStringProperty(c.getValue().getDriverName()));
		
		TableColumn<Trip, String> originCol = new TableColumn<>("Origin");
		originCol.setCellValueFactory(c -> 
				new javafx.beans.property.SimpleStringProperty(c.getValue().getOrigin()));
		
		TableColumn<Trip, String> destCol = new TableColumn<>("Destination");
		destCol.setCellValueFactory(c -> 
				new javafx.beans.property.SimpleStringProperty(c.getValue().getDestination()));
		
		TableColumn<Trip, String> milesCol = new TableColumn<>("Miles");
		milesCol.setCellValueFactory(c -> 
				new javafx.beans.property.SimpleStringProperty(String.format("%.1f", c.getValue().getMiles())));
		
		table.getColumns().addAll(List.of(dateCol, driverCol, originCol, destCol, milesCol));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
		table.setPlaceholder(new Label("No trips logged for this vehicle yet"));
	}
	
	private void loadVehicles() {
		try {
			List<Vehicle> vehicles =  vehicleDAO.findAll(null, null);
			vehicleBox.setItems(FXCollections.observableArrayList(vehicles));
			if (!vehicles.isEmpty()) vehicleBox.getSelectionModel().selectFirst();
		} catch (SQLException e) {
			e.printStackTrace();
			countLabel.setText("Could not load vehicles");
		}
	}
	
	private void loadHistory() {
		Vehicle v = vehicleBox.getValue();
		if (v == null) return;
		try {
			List<Trip> trips =  tripDAO.findByVehicle(v.getVehicleId());
			table.setItems(FXCollections.observableArrayList(trips));
			historyTitle.setText("TRIP HISTORY — " + v.getMakeModel() + " (" + displayId(v) +")");
			countLabel.setText("Showing " + trips.size() + " trip(s)");
		} catch (SQLException e) {
			e.printStackTrace();
			countLabel.setText("Could not load trips");
		}
		
	}
	
	private String displayId(Vehicle v) {
		return String.format("V-%03d", v.getVehicleId());
	}

}
