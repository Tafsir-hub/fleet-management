package com.tafsir.fleetmanagement.ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.dao.MaintenanceDAO;
import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.model.MaintenanceRecord;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;
import com.tafsir.fleetmanagement.ui.components.DateField;
import com.tafsir.fleetmanagement.ui.components.TableColumns;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

// The maintenance tab or the Fuel & Maintenance screen
public class MaintenancePane extends BorderPane {
	
	private static final DateTimeFormatter DATE_FMT = 
			DateTimeFormatter.ofPattern("MM/dd/yyyy");
	
	private static final String[] SERVICE_TYPES = {"Oil change", "Tire rotation", "Tire replacement",
        "Brake inspection", "Brake system overhaul", "Air filter replacement", "Coolant flush",
        "Annual inspection", "Engine repair", "Other"};
	
	// Form
	private final ComboBox<Vehicle> vehicleBox = new ComboBox<>();
	private final DateField dateField = new DateField();
	private final ComboBox<String> serviceBox = new ComboBox<>();
	private final TextField otherField = new TextField();
	private final TextField costField = new TextField();
	private final TextField mileageField = new TextField();
	private final Button saveButton = new Button("Save Maintenance Record");
	private VBox otherBox; 
	
	
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
		buildVehicleBox();
		setTop(buildForm());
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
	
	private VBox buildForm() {
		dateField.setValue(LocalDate.now());
		dateField.setMaxDate(LocalDate.now());
		serviceBox.setPromptText("Select a service");
		serviceBox.getItems().addAll(SERVICE_TYPES);
		otherField.setPromptText("Describe the service");
		costField.setPromptText("133.50");
		mileageField.setPromptText("12000");
		
		// "Other" lets the user type their own service type
		otherBox = field("DESCRIBE SERVICE", otherField);
		otherBox.setVisible(false);
		otherBox.setManaged(false);
		serviceBox.valueProperty().addListener((obs, old, v) -> {
			boolean other = "Other".equals(v);
			otherBox.setVisible(other);
			otherBox.setManaged(other);
		});
		
		GridPane form = new GridPane();
		form.setHgap(16);
		form.setVgap(12);
		for (int i = 0; i <3; i++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setPercentWidth(100.0 / 3);
			form.getColumnConstraints().add(cc);	
		}
		form.add(field("VEHICE", vehicleBox), 0, 0);
		form.add(field("DATE", dateField), 1, 0);
		form.add(field("SERVICE TYPE", serviceBox), 2, 0);
		form.add(field("COST ($)", costField), 0, 1);
		form.add(field("MILEAGE", mileageField),  1, 1);
		form.add(otherBox, 2, 1);
		
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		HBox buttonRow = new HBox(12, spacer, saveButton);
		buttonRow.setAlignment(Pos.CENTER_RIGHT);
		
		Label formTitle = new Label("NEW MAINTENANCE RECORD");
		formTitle.getStyleClass().add("section-title");
		VBox formBox = new VBox(12, formTitle, form, buttonRow);
		formBox.setPadding(new Insets(0, 0, 20, 0));
		return formBox;
		}
	
	private void buildVehicleBox() {
		vehicleBox.setPromptText("Select a vehicle");
		vehicleBox.setConverter(new StringConverter<Vehicle>() {
			@Override public String toString(Vehicle v) {
				return v == null ? "" : v.getMakeModel() + " (" + String.format("V-%03d", v.getVehicleId()) +")";
			}
			@Override public Vehicle fromString(String s) {return null;}
		});
	}
	
	// Small label above a control, like the other forms
	private VBox field(String caption, Node control) {
        if (control instanceof Region) {
            ((Region) control).setMaxWidth(Double.MAX_VALUE);
        }
        return new VBox(4, new Label(caption), control);
    }
	
	// Service can be recorded on any vehicle that is not Retired (including In Repair)
	private List<Vehicle> notRetired(List<Vehicle> all) {
		List<Vehicle> list = new ArrayList<>();
		for (Vehicle x : all) {
			if (x.getStatus() != VehicleStatus.RETIRED) list.add(x);
		}
		return list;
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
        vehicleBox.setItems(FXCollections.observableArrayList(notRetired(vehicles)));
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
