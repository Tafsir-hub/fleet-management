package com.tafsir.fleetmanagement.ui;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.dao.MaintenanceDAO;
import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.logic.Validator;
import com.tafsir.fleetmanagement.model.MaintenanceRecord;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;
import com.tafsir.fleetmanagement.ui.components.DateField;
import com.tafsir.fleetmanagement.ui.components.TableColumns;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
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
        vehicleBox.valueProperty().addListener((obs, old, v) ->
        mileageField.setPromptText(v == null ? "12000" : String.format("Up to %,d", v.getMileage())));
		serviceBox.getItems().addAll(SERVICE_TYPES);
        serviceBox.setButtonCell(new ListCell<String>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Select a service" : item);
            }
        });
		otherField.setPromptText("Describe the service");
		costField.setPromptText("133.50");
		mileageField.setPromptText("12000");
		saveButton.setOnAction(e -> saveRecord());
		
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
		form.add(field("VEHICLE", vehicleBox), 0, 0);
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
	
    // ---------- Saving ----------

    private void saveRecord() {
        Vehicle v = vehicleBox.getValue();
        LocalDate date = dateField.getDate();
        String service = serviceBox.getValue();

        // Checks in field order; the first problem is shown
        if (v == null) { fail("Choose a vehicle", vehicleBox); return; }
        if (date == null) { fail("Enter a valid date, like 11/15/2026", dateField); return; }
        if (!Validator.validTripDate(date)) { fail("Service date cannot be in the future", dateField); return; }
        if (service == null) { fail("Choose a service type", serviceBox); return; }

        if ("Other".equals(service)) {
            service = otherField.getText().trim();
            if (service.isEmpty() || service.equalsIgnoreCase("Other")) { fail("Describe the service", otherField); return; }
            if (service.length() > 60) { fail("The service description can have at most 60 characters", otherField); return; }
            // Use the standard spelling if it matches a listed type
            for (String type : SERVICE_TYPES) {
                if (type.equalsIgnoreCase(service)) service = type;
            }
        
        }

        String costText = costField.getText().trim();
        double cost;
        try {
            cost = Double.parseDouble(costText);
        } catch (NumberFormatException ex) {
            fail("Cost must be a number, like 133.50", costField);
            return;
        }
        if (!Validator.validCost(cost)) { fail("Cost must be greater than 0", costField); return; }
        if (decimals(costText) > 2) { fail("Cost can have at most 2 decimals, like 133.50", costField); return; }
        if (cost > 999999.99) { fail("Cost cannot be more than 999,999.99", costField); return; }

        int mileage;
        try {
            mileage = Integer.parseInt(mileageField.getText().trim());
        } catch (NumberFormatException ex) {
            fail("Mileage must be a whole number, like 12000", mileageField);
            return;
        }
        if (mileage < 0) { fail("Mileage cannot be negative", mileageField); return; }
        
        // The screen may be out of date: re-read the vehicle (it may be Retired now, or its mileage may have changed)
        try {
            Vehicle fresh = null;
            for (Vehicle x : vehicleDAO.findAll(null, null)) {
                if (x.getVehicleId() == v.getVehicleId()) fresh = x;
            }
            if (fresh == null || fresh.getStatus() == VehicleStatus.RETIRED) {
                loadVehicles();
                vehicleBox.setValue(null);
                messageBar.error(vehicleLabel(v.getVehicleId()) + " is Retired. Choose another vehicle.");
                return;
            }
            v = fresh;
        } catch (SQLException e) {
            e.printStackTrace();
            messageBar.error("Could not check the vehicle");
            return;
        }

        if (mileage > v.getMileage()) {
            fail(String.format("Mileage cannot be more than the vehicle's current mileage (%,d). Update the vehicle first.",
                    v.getMileage()), mileageField);
            return;
        }

        // Go Back (or closing the dialog) stops here; nothing is saved
        if (!confirmSave(v, date, service, cost, mileage)) return;

        try {
            int id = maintenanceDAO.addRecord(new MaintenanceRecord(0, v.getVehicleId(), date, service, cost, mileage));
            costField.clear();
            mileageField.clear();
            otherField.clear();
            serviceBox.setValue(null);
            loadRecords();
            boolean shown = selectRecord(id);
            messageBar.success(String.format("Saved maintenance record: %s, %s, $%,.2f",
                    vehicleLabel(v.getVehicleId()), service, cost)
                    + (shown ? "" : " (not shown in the history because of the current filter)"));
        } catch (SQLException e) {
            e.printStackTrace();
            messageBar.error("Could not save the maintenance record");
        }
    }

    // Red message plus the cursor on the field that needs fixing
    private void fail(String text, Node field) {
        messageBar.error(text);
        field.requestFocus();
    }

    // Number of digits after the decimal point in what the user typed ("133.500" counts as 2)
    private int decimals(String text) {
        try {
            return Math.max(0, new BigDecimal(text).stripTrailingZeros().scale());
        } catch (NumberFormatException e) {
            return 99;   // things like "12d" or "NaN" are not plain numbers
        }
    }

    // Highlights the new row in the history
    private boolean selectRecord(int maintId) {
        for (MaintenanceRecord r : table.getItems()) {
            if (r.getMaintId() == maintId) {
                table.getSelectionModel().select(r);
                table.scrollTo(r);
                return true;
            }
        }
        return false;
    }
    
    private boolean confirmSave(Vehicle v, LocalDate date, String service, double cost, int mileage) {
        ButtonType save = new ButtonType("Save Record", ButtonBar.ButtonData.OK_DONE);
        ButtonType back = new ButtonType("Go Back", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", save, back);
        alert.initOwner(getScene().getWindow());
        alert.setTitle("FMAS");
        alert.setHeaderText("Save this maintenance record?");

        Label details = new Label(
                "Vehicle: " + vehicleLabel(v.getVehicleId()) + "\n"
              + "Date: " + date.format(DATE_FMT) + "\n"
              + "Service: " + service + "\n"
              + String.format("Cost: $%,.2f\n", cost)
              + String.format("Mileage: %,d", mileage));

        Label warning = new Label("A saved maintenance record cannot be changed or deleted.");
        warning.setStyle("-fx-text-fill: #b3261e; -fx-font-weight: bold;");

        alert.getDialogPane().setContent(new VBox(12, details, warning));

        // Go Back is the default button, so pressing Enter by accident does not save
        ((Button) alert.getDialogPane().lookupButton(save)).setDefaultButton(false);
        ((Button) alert.getDialogPane().lookupButton(back)).setDefaultButton(true);

        return alert.showAndWait().filter(b -> b == save).isPresent();
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
