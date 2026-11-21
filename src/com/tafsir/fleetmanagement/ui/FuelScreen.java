package com.tafsir.fleetmanagement.ui;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.tafsir.fleetmanagement.dao.FuelDAO;
import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.logic.Validator;
import com.tafsir.fleetmanagement.model.FuelEntry;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;
import com.tafsir.fleetmanagement.ui.components.DateField;
import com.tafsir.fleetmanagement.ui.components.TabSwitch;
import com.tafsir.fleetmanagement.ui.components.TableColumns;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
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

public class FuelScreen extends BorderPane {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final FuelDAO fuelDAO = new FuelDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();
    
    // Form
    private final ComboBox<Vehicle> vehicleBox = new ComboBox<>();
    private final DateField dateField = new DateField();
    private final TextField gallonsField = new TextField();
    private final TextField priceField = new TextField();
    private final Label totalLabel = new Label();
    private final Button saveButton = new Button("Save Fuel Entry");

    // History
    private final ComboBox<String> vehicleFilter = new ComboBox<>();
    private final TableView<FuelEntry> table = new TableView<>();
    private final Label historyTitle = new Label();
    private final Label countLabel = new Label();
    private final MessageBar messageBar = new MessageBar();
    private final BorderPane fuelPane = new BorderPane();
    private final MaintenancePane maintenancePane = new MaintenancePane();
    private TabSwitch tabs;

    private List<Vehicle> vehicles = new ArrayList<>();

    public FuelScreen(Consumer<String> onSection) {
        setPadding(new Insets(18, 20, 14, 20));
        
        tabs = new TabSwitch(tab -> { showTab(tab); onSection.accept(tab); }, "Fuel", "Maintenance");
        setTop(tabs);
        
        setTop(tabs);
        BorderPane.setMargin(tabs, new Insets(0, 0, 16, 0));

        buildVehicleBox();
        buildFuelPane();
        showTab("Fuel");

        loadVehicles();
        vehicleFilter.valueProperty().addListener((obs, old, v) -> loadEntries());
        loadEntries();
    }

    private void showTab(String tab) {
        setCenter("Maintenance".equals(tab) ? maintenancePane : fuelPane);
    }
    
    public void showMaintenanceTab() {
    	tabs.select("Maintenance");
    }
    
    private void buildFuelPane() {
    	fuelPane.setTop(buildForm());
    	fuelPane.setCenter(buildHistory());
    }
  //----------------Form---------------------

    private VBox buildForm() {
    	    	
    	dateField.setValue(LocalDate.now());
    	dateField.setMaxDate(LocalDate.now());
    	gallonsField.setPromptText("18.4");
    	priceField.setPromptText("3.62");
    	gallonsField.textProperty().addListener((obs, old, v) -> updateTotal());
    	priceField.textProperty().addListener((obs, old, v) -> updateTotal());
    	updateTotal();
    	saveButton.setOnAction(e -> saveFuel());
    	
    	GridPane form = new GridPane();
    	form.setHgap(16);
    	form.setVgap(12);
    	for (int i = 0; i < 4; i++) {
    		ColumnConstraints  cc = new ColumnConstraints();
    		cc.setPercentWidth(25);
    		form.getColumnConstraints().add(cc);
    	}
    	form.add(field("VEHICLE", vehicleBox), 0, 0);
    	form.add(field("DATE", dateField), 1, 0);
    	form.add(field("GALLONS", gallonsField), 2, 0);
    	form.add(field("PRICE / GAL ($)", priceField) , 3, 0);
    	
    	totalLabel.setStyle("-fx-font-weight: bold;");
    	Region spacer = new Region();
    	HBox.setHgrow(spacer, Priority.ALWAYS);
    	HBox buttonRow = new HBox(12, totalLabel, spacer, saveButton);
    	buttonRow.setAlignment(Pos.CENTER_LEFT);
    	
    	Label formTitle = new Label("NEW FUEL ENTRY");
    	formTitle.getStyleClass().add("section-title");
    	VBox formBox = new VBox(12, formTitle, form, buttonRow);
    	formBox.setPadding(new Insets(0, 0, 20, 0));
    	return formBox;
    }
        
    // ---------------History-------------------
    
    private VBox buildHistory() {
    	
    	historyTitle.getStyleClass().add("section-title");
        vehicleFilter.setPrefWidth(320);

        HBox header = new HBox(12, historyTitle, new Label("Vehicle"), vehicleFilter);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 10, 0));

        table.getColumns().add(TableColumns.<FuelEntry>formatted("Date",
                f -> f.getFuelDate().format(DATE_FMT), 110));
        table.getColumns().add(TableColumns.<FuelEntry>formatted("Vehicle",
                f -> vehicleLabel(f.getVehicleId()), 280));
        table.getColumns().add(TableColumns.<FuelEntry>formatted("Gallons",
                f -> String.format("%.1f", f.getGallons()), 90));
        table.getColumns().add(TableColumns.<FuelEntry>formatted("Price / Gal",
                f -> String.format("$%.2f", f.getPricePerGallon()), 100));
        table.getColumns().add(TableColumns.<FuelEntry>formatted("Total Cost",
                f -> String.format("$%,.2f", f.getTotalCost()), 110));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No fuel entries for this vehicle"));

        countLabel.getStyleClass().add("muted");
        VBox bottom = new VBox(8, messageBar, countLabel);
        bottom.setPadding(new Insets(8, 0, 0, 0));

        VBox history = new VBox(8, header, table, bottom);
        VBox.setVgrow(table, Priority.ALWAYS);
        return history;
    }
    	    
    private void buildVehicleBox() {
    	vehicleBox.setPromptText("Select a vehicle");
    	vehicleBox.setConverter(new StringConverter<Vehicle>() {
    		@Override public String toString(Vehicle v) {
    			return v == null ? "" : v.getMakeModel() + " (" + String.format("V-%03d", v.getVehicleId()) + ")";
    		}
    		@Override public Vehicle fromString(String s) {
    			return null;}
    		});
    }
    
    // Small label above a control, like the Trips form
    private VBox field(String caption, Node control) {
    	if (control instanceof Region) {
    		((Region) control).setMaxWidth(Double.MAX_VALUE);
    	}
    	return new VBox(4, new Label(caption), control);
    }
    
    // Recalculates "Total: $x.xx" every time gallons or price changes
    private void updateTotal() {
    	try {
    		double g = Double.parseDouble(gallonsField.getText().trim());
    		double p = Double.parseDouble(priceField.getText().trim());
    		if (Validator.validGallons(g) && Validator.validCost(p)) {
    			totalLabel.setText(String.format("Total: $%,.2f (auto-calculated)", FuelEntry.calculateTotal(g, p)));
    			return;
    		}
    	} catch (NumberFormatException e) {
    		// nothing to calculate yet
    	}
    	totalLabel.setText("Total: $0.00 (auto-calculated)");
    }
    
    private void saveFuel() {
    	Vehicle v = vehicleBox.getValue();
    	LocalDate date = dateField.getDate();
    	
    	// Checks in field order; the first problem is shown
    	if (v == null) { fail("Choose a vehicle", vehicleBox); return;}
    	if (date == null) {fail("Enter a valid date, like 11/15/2026", dateField); return;}
    	if (!Validator.validTripDate(date)) {
    		fail("Fuel date cannot be in the future", dateField); return;}
    	
    	String gallonsText = gallonsField.getText().trim();
    	String priceText = priceField.getText().trim();
    	
    	double gallons;
    	try {
    		gallons = Double.parseDouble(gallonsText);	
    	} catch (NumberFormatException ex) {
    		fail("Gallons must be a number, like 18.4", gallonsField);
    		return;    		
    	}
    	if (!Validator.validGallons(gallons)) { fail("Gallons must be greater than 0", gallonsField); return;}
    	if (decimals(gallonsText) > 1) {fail("Gallons can have at most 1 decimal, like 18.4", gallonsField); return;}
    	
    	double price;
    	try {
    		price = Double.parseDouble(priceText);
    	} catch (NumberFormatException ex) {
    		fail("Price per gallon must be a number, like 3.62", priceField);
    		return;
    	}
        if (!Validator.validCost(price)) { fail("Price per gallon must be greater than 0", priceField); return;}
    	if (decimals(priceText) > 2) {fail("Price per gallon can have at most 2 decimals, like 3.62", priceField); return;}
    	
    	try {
    		if (!checkStillActive(v)) return;
    	} catch (SQLException e) {
    		e.printStackTrace();
    		messageBar.error("Could not check the vehicle");
    		return;
    	}
    	
    	// Go back (or closing the dialog) stops here; nothing is saved
    	double total = FuelEntry.calculateTotal(gallons, price);
    	if(!confirmSave(v, date, gallons, price, total)) return;
    	
    	try {
    		int id = fuelDAO.addFuel(new FuelEntry(0, v.getVehicleId(), date, gallons, price));
    		gallonsField.clear();
    		priceField.clear();
    		loadEntries();
    		boolean shown = selectEntry(id);
    		messageBar.success(String.format("Saved fuel entry: %s, %.1f gal, $%,.2f",
    				vehicleLabel(v.getVehicleId()), gallons, total)
    				+ (shown ? "" : " (not shown in the history because of the current filter)"));
    	} catch (SQLException e) {
    		e.printStackTrace();
    		messageBar.error("Could not save the fuel entry");
    	}
    }
    
    // Red message plus the cursor on the field that needs fixing
    private void fail(String text, Node field) {
    	messageBar.error(text);
    	field.requestFocus();
    	
    }
    
    private boolean checkStillActive(Vehicle v) throws SQLException {
    	boolean active = false;
    	for (Vehicle x : vehicleDAO.findAll(null, null)) {
    		if (x.getVehicleId() == v.getVehicleId() && x.getStatus() == VehicleStatus.ACTIVE) {
                active = true;
            }
    	}
    	if (active) return true;
    	
    	// Refresh the dropdown and clear the stale pick
    	vehicleBox.setItems(FXCollections.observableArrayList(activeOnly(vehicleDAO.findAll(null, null))));
    	vehicleBox.setValue(null);
    	messageBar.error(vehicleLabel(v.getVehicleId()) + " is no longer Active. Choose another vehicle.");
    	return false;
    }
    
    private boolean confirmSave(Vehicle v, LocalDate date, double gallons, double price, double total) {
    	ButtonType save = new ButtonType("Save Entry", ButtonBar.ButtonData.OK_DONE);
    	ButtonType back = new ButtonType("Go Back", ButtonBar.ButtonData.CANCEL_CLOSE);
    	
    	Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", save, back);
    	alert.initOwner(getScene().getWindow());
    	alert.setTitle("FMAS");
    	alert.setHeaderText("Save this fuel entry?");
    	
    	Label details = new Label(
    			"Vehicle: " + vehicleLabel(v.getVehicleId()) + "\n"
    			+ "Date: " + date.format(DATE_FMT) + "\n"
    			+ String.format("Gallons: %.1f\n", gallons)
    			+ String.format("Price per gallon: $%.2f\n", price)
    			+String.format("Total cost: $%,.2f", total));
    	Label warning = new Label("A saved fuel entry cannot be changed or deleted.");
    	warning.setStyle("-fx-text-fill: #b3261e; -fx-font-weight: bold;");
    	
    	alert.getDialogPane().setContent(new VBox(12, details, warning));
    	
    	// Go back is the default button, so pression Enter by accident does not save 
    	((Button) alert.getDialogPane().lookupButton(save)).setDefaultButton(false);
    	((Button) alert.getDialogPane().lookupButton(back)).setDefaultButton(true);
    	
    	return alert.showAndWait().filter(b -> b == save).isPresent();
    }
    
    private int decimals(String text) {
    	try {
    		return Math.max(0, new BigDecimal(text).stripTrailingZeros().scale());
    	} catch (NumberFormatException e) {
    		return 99; // things like "18d" or "NaN" are not plain numbers
    	}
    }
    
    // Highlights the new row in the history
    private boolean selectEntry(int fuelId) {
    	for (FuelEntry f : table.getItems()) {
    		if (f.getFuelId() == fuelId) {
    			table.getSelectionModel().select(f);
    			table.scrollTo(f);
    			return true;
    		}
    	}
    	return false;
    }
        
    private List<Vehicle> activeOnly(List<Vehicle> all) {
    	List<Vehicle> active = new ArrayList<>();
    	for (Vehicle x : all) {
    		if (x.getStatus() == VehicleStatus.ACTIVE) active.add(x);
    	}
    	return active;
    }
    
    // Fills the vehicle filter: "All vehicles" first, then every vehicle (history includes inactive ones)
    private void loadVehicles() {
        try {
            vehicles = vehicleDAO.findAll(null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            vehicles = new ArrayList<>();
        }
        vehicleBox.setItems(FXCollections.observableArrayList(activeOnly(vehicles)));
        List<String> labels = new ArrayList<>();
        labels.add("All vehicles");
        for (Vehicle v : vehicles) labels.add(vehicleLabel(v.getVehicleId()));
        vehicleFilter.setItems(FXCollections.observableArrayList(labels));
        vehicleFilter.getSelectionModel().selectFirst();
    }

    private void loadEntries() {
        int idx = vehicleFilter.getSelectionModel().getSelectedIndex();
        if (idx < 0) return;
        try {
            List<FuelEntry> entries;
            if (idx == 0) {
                entries = fuelDAO.findAll();
                historyTitle.setText("RECENT ENTRIES — All vehicles");
            } else {
                int vehicleId = vehicles.get(idx - 1).getVehicleId();
                entries = fuelDAO.findByVehicle(vehicleId);
                historyTitle.setText("RECENT ENTRIES — " + vehicleLabel(vehicleId));
            }
            table.setItems(FXCollections.observableArrayList(entries));
            countLabel.setText("Showing " + entries.size() + " fuel entry(ies)");
        } catch (SQLException e) {
            e.printStackTrace();
            countLabel.setText("Could not load fuel entries");
        }
    }

    private String vehicleLabel(int vehicleId) {
        for (Vehicle v : vehicles) {
            if (v.getVehicleId() == vehicleId)
                return v.getMakeModel() + " (" + String.format("V-%03d", vehicleId) + ")";
        }
        return String.format("V-%03d", vehicleId);
    }
}