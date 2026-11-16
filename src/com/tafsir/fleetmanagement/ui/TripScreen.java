package com.tafsir.fleetmanagement.ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.dao.DriverDAO;
import com.tafsir.fleetmanagement.dao.TripDAO;
import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.logic.Validator;
import com.tafsir.fleetmanagement.model.Driver;
import com.tafsir.fleetmanagement.model.Trip;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;
import com.tafsir.fleetmanagement.ui.components.DateField;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.ListCell;
import javafx.scene.control.TableColumn;
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

public class TripScreen extends BorderPane {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final VehicleDAO vehicleDAO = new VehicleDAO();
    private final DriverDAO driverDAO = new DriverDAO();
    private final TripDAO tripDAO = new TripDAO();

    // Form
    private final ComboBox<Vehicle> vehicleBox = new ComboBox<>();
    private final ComboBox<Driver> driverBox = new ComboBox<>();
    private final DateField dateField = new DateField();
    private final TextField originField = new TextField();
    private final TextField destinationField = new TextField();
    private final TextField milesField = new TextField();
    private final Button saveButton = new Button("Save Trip");
    private final MessageBar message = new MessageBar();

    // History
    private final Label historyTitle = new Label("TRIP HISTORY");
    private final TableView<Trip> table = new TableView<>();
    private final Label countLabel = new Label();
    private final ComboBox<String> vehicleFilter = new ComboBox<>();
    private final ComboBox<String> driverFilter = new ComboBox<>();
    private List<Driver> allDrivers = new ArrayList<>();
    private List<Vehicle> vehicles = new ArrayList<>();

    public TripScreen() {
        setPadding(new Insets(24));

        buildVehicleBox();
        buildDriverBox();
        buildTable();

        dateField.setValue(LocalDate.now());
        dateField.setMaxDate(LocalDate.now());
        originField.setPromptText("Newark, NJ");
        destinationField.setPromptText("Philadelphia, PA");
        milesField.setPromptText("94.5");
        saveButton.setOnAction(e -> saveTrip());

        // Form
        GridPane form = new GridPane();
        form.setHgap(16);
        form.setVgap(12);
        for (int i = 0; i < 3; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 3);
            form.getColumnConstraints().add(cc);
        }
        form.add(field("VEHICLE", vehicleBox), 0, 0);
        form.add(field("DRIVER", driverBox), 1, 0);
        form.add(field("DATE", dateField), 2, 0);
        form.add(field("ORIGIN", originField), 0, 1);
        form.add(field("DESTINATION", destinationField), 1, 1);
        form.add(field("MILES", milesField), 2, 1);

        HBox buttonRow = new HBox(saveButton);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        Label formTitle = new Label("NEW TRIP");
        formTitle.getStyleClass().add("section-title");
        historyTitle.getStyleClass().add("section-title");

        VBox formBox = new VBox(12, formTitle, form, buttonRow);
        formBox.setPadding(new Insets(0, 0, 20, 0));

        // History header with the two filters
        vehicleFilter.setPrefWidth(320);
        driverFilter.setPrefWidth(200);
        vehicleFilter.valueProperty().addListener((obs, old, v) -> loadHistory());
        driverFilter.valueProperty().addListener((obs, old, v) -> loadHistory());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox historyHeader = new HBox(12, historyTitle, spacer,
                new Label("Vehicle"), vehicleFilter, new Label("Driver"), driverFilter);
        historyHeader.setAlignment(Pos.CENTER_LEFT);

        VBox center = new VBox(8, historyHeader, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        // Message bar and count under the table, like the Drivers screen
        countLabel.getStyleClass().add("muted");
        VBox bottom = new VBox(8, message, countLabel);
        bottom.setPadding(new Insets(8, 0, 0, 0));

        setTop(formBox);
        setCenter(center);
        setBottom(bottom);

        loadVehicles();
        loadDrivers();
    }

    // Small label above a control, like the concept screen
    private VBox field(String caption, Node control) {
        if (control instanceof Region) {
            ((Region) control).setMaxWidth(Double.MAX_VALUE);
        }
        return new VBox(4, new Label(caption), control);
    }

    private void buildVehicleBox() {
        vehicleBox.setPromptText("Select a vehicle");
        vehicleBox.setConverter(new StringConverter<Vehicle>() {
            @Override public String toString(Vehicle v) {
                return v == null ? "" : v.getMakeModel() + " (" + displayId(v) + ")";
            }
            @Override public Vehicle fromString(String s) { return null; }
        });
    }

    private void buildDriverBox() {
        driverBox.setPromptText("Select a driver");
        driverBox.setConverter(new StringConverter<Driver>() {
            @Override public String toString(Driver d) {
                return d == null ? "" : d.getShortName();
            }
            @Override public Driver fromString(String s) { return null; }
        });
        driverBox.setButtonCell(new ListCell<Driver>() {
            @Override protected void updateItem(Driver d, boolean empty) {
                super.updateItem(d, empty);
                setText(empty || d == null ? "Select a driver" : d.getShortName());
            }
        });
    }

    private void buildTable() {
        TableColumn<Trip, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getTripDate().format(DATE_FMT)));
        TableColumn<Trip, String> vehicleCol = new TableColumn<>("Vehicle");
        vehicleCol.setCellValueFactory(c ->
                new SimpleStringProperty(vehicleLabel(c.getValue().getVehicleId())));
        TableColumn<Trip, String> driverCol = new TableColumn<>("Driver");
        driverCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDriverName()));
        TableColumn<Trip, String> originCol = new TableColumn<>("Origin");
        originCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getOrigin()));
        TableColumn<Trip, String> destCol = new TableColumn<>("Destination");
        destCol.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getDestination()));
        TableColumn<Trip, String> milesCol = new TableColumn<>("Miles");
        milesCol.setCellValueFactory(c ->
                new SimpleStringProperty(String.format("%.1f", c.getValue().getMiles())));

        table.getColumns().addAll(List.of(dateCol, vehicleCol, driverCol, originCol, destCol, milesCol));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPlaceholder(new Label("No trips match the current filters"));
    }

    // ---------- Loading ----------

    private void loadVehicles() {
        try {
            vehicles = vehicleDAO.findAll(null, null);

            // History filter lists every vehicle; the form lists Active ones only
            List<String> labels = new ArrayList<>();
            labels.add("All vehicles");
            for (Vehicle v : vehicles) labels.add(vehicleLabel(v.getVehicleId()));
            vehicleFilter.setItems(FXCollections.observableArrayList(labels));
            vehicleFilter.getSelectionModel().selectFirst();

            vehicleBox.setItems(FXCollections.observableArrayList(activeOnly(vehicles)));
        } catch (SQLException e) {
            e.printStackTrace();
            message.error("Could not load vehicles");
        }
    }

    private void loadDrivers() {
        try {
            // Only Active drivers can be picked in the form
            driverBox.setItems(FXCollections.observableArrayList(driverDAO.findActive()));

            // History filter includes Inactive drivers
            allDrivers = driverDAO.findAll();
            List<String> labels = new ArrayList<>();
            labels.add("All drivers");
            for (Driver d : allDrivers) labels.add(d.getFullName());
            driverFilter.setItems(FXCollections.observableArrayList(labels));
            driverFilter.getSelectionModel().selectFirst();
        } catch (SQLException e) {
            e.printStackTrace();
            message.error("Could not load drivers");
        }
    }

    private void loadHistory() {
        int vIdx = vehicleFilter.getSelectionModel().getSelectedIndex();
        int dIdx = driverFilter.getSelectionModel().getSelectedIndex();
        if (vIdx < 0 || dIdx < 0) return;

        Integer vehicleId = (vIdx == 0) ? null : vehicles.get(vIdx - 1).getVehicleId();
        Integer driverId = (dIdx == 0) ? null : allDrivers.get(dIdx - 1).getDriverId();
        try {
            List<Trip> trips = tripDAO.find(vehicleId, driverId);
            String title = "TRIP HISTORY — " + (vIdx == 0 ? "All vehicles" : vehicleLabel(vehicleId));
            if (dIdx > 0) title += ", " + allDrivers.get(dIdx - 1).getFullName();
            historyTitle.setText(title);
            table.setItems(FXCollections.observableArrayList(trips));
            countLabel.setText("Showing " + trips.size() + " trip(s)");
        } catch (SQLException e) {
            e.printStackTrace();
            countLabel.setText("Could not load trips");
        }
    }

    // ---------- Saving a trip ----------

    private void saveTrip() {
        Vehicle v = vehicleBox.getValue();
        Driver d = driverBox.getValue();
        LocalDate date = dateField.getDate();
        String origin = originField.getText().trim();
        String destination = destinationField.getText().trim();

        // Checks in field order; the first problem is shown
        if (v == null) { fail("Choose a vehicle", vehicleBox); return; }
        if (d == null) { fail("Choose a driver", driverBox); return; }
        if (date == null) { fail("Enter a valid date, like 11/15/2026", dateField); return; }
        if (!Validator.validTripDate(date)) { fail("Trip date cannot be in the future", dateField); return; }
        if (origin.isEmpty()) { fail("Origin is required", originField); return; }
        if (destination.isEmpty()) { fail("Destination is required", destinationField); return; }

        double miles;
        try {
            miles = Double.parseDouble(milesField.getText().trim());
        } catch (NumberFormatException ex) {
            fail("Miles must be a number, like 94.5", milesField);
            return;
        }
        if (!Validator.validTripMiles(miles)) { fail("Miles must be greater than 0", milesField); return; }

        // The screen may be out of date: make sure the vehicle and driver are still Active
        try {
            if (!checkStillActive(v, d)) return;
        } catch (SQLException e) {
            e.printStackTrace();
            message.error("Could not check the vehicle and driver");
            return;
        }

        // Go Back (or closing the dialog) stops here; nothing is saved
        if (!confirmSave(v, d, date, origin, destination, miles)) return;

        try {
            int id = tripDAO.addTrip(new Trip(0, v.getVehicleId(), d.getDriverId(),
                    date, origin, destination, miles));
            originField.clear();
            destinationField.clear();
            milesField.clear();
            driverBox.setValue(null);
            loadHistory();
            boolean shown = selectTrip(id);
            message.success("Logged trip: " + origin + " to " + destination + ", " + miles + " mi"
                    + (shown ? "" : " (not shown in the history because of the current filters)"));
        } catch (SQLException e) {
            e.printStackTrace();
            message.error("Could not save the trip");
        }
    }

    // Red message plus the cursor on the field that needs fixing
    private void fail(String text, Node field) {
        message.error(text);
        field.requestFocus();
    }

    // Re-reads the database: a vehicle or driver may have changed status since the screen opened
    private boolean checkStillActive(Vehicle v, Driver d) throws SQLException {
        boolean vehicleActive = false;
        for (Vehicle x : vehicleDAO.findAll(null, null)) {
            if (x.getVehicleId() == v.getVehicleId() && x.getStatus() == VehicleStatus.ACTIVE) {
                vehicleActive = true;
            }
        }
        boolean driverActive = false;
        for (Driver x : driverDAO.findActive()) {
            if (x.getDriverId() == d.getDriverId()) driverActive = true;
        }

        if (vehicleActive && driverActive) return true;

        // Refresh the dropdowns and clear whichever pick went stale
        vehicleBox.setItems(FXCollections.observableArrayList(activeOnly(vehicleDAO.findAll(null, null))));
        driverBox.setItems(FXCollections.observableArrayList(driverDAO.findActive()));
        if (!vehicleActive) {
            vehicleBox.setValue(null);
            message.error(vehicleLabel(v.getVehicleId()) + " is no longer Active. Choose another vehicle.");
        } else {
            driverBox.setValue(null);
            message.error(d.getFullName() + " is no longer Active. Choose another driver.");
        }
        return false;
    }

    private boolean confirmSave(Vehicle v, Driver d, LocalDate date, String origin,
            String destination, double miles) {
        ButtonType save = new ButtonType("Save Trip", ButtonBar.ButtonData.OK_DONE);
        ButtonType back = new ButtonType("Go Back", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", save, back);
        alert.initOwner(getScene().getWindow());
        alert.setTitle("FMAS");
        alert.setHeaderText("Save this trip?");

        Label details = new Label(
                "Vehicle: " + vehicleLabel(v.getVehicleId()) + "\n"
              + "Driver: " + d.getFullName() + "\n"
              + "Date: " + date.format(DATE_FMT) + "\n"
              + "Route: " + origin + " to " + destination + "\n"
              + "Miles: " + miles);

        Label warning = new Label("A saved trip cannot be changed or deleted.");
        warning.setStyle("-fx-text-fill: #b3261e; -fx-font-weight: bold;");

        alert.getDialogPane().setContent(new VBox(12, details, warning));

        ((Button) alert.getDialogPane().lookupButton(save)).setDefaultButton(false);
        ((Button) alert.getDialogPane().lookupButton(back)).setDefaultButton(true);

        return alert.showAndWait().filter(b -> b == save).isPresent();
    }

    // Highlights the new row in the history
    private boolean selectTrip(int tripId) {
        for (Trip t : table.getItems()) {
            if (t.getTripId() == tripId) {
                table.getSelectionModel().select(t);
                table.scrollTo(t);
                return true;
            }
        }
        return false;
    }

    // ---------- Helpers ----------

    private List<Vehicle> activeOnly(List<Vehicle> all) {
        List<Vehicle> active = new ArrayList<>();
        for (Vehicle x : all) {
            if (x.getStatus() == VehicleStatus.ACTIVE) active.add(x);
        }
        return active;
    }

    private String displayId(Vehicle v) {
        return String.format("V-%03d", v.getVehicleId());
    }

    private String vehicleLabel(int vehicleId) {
        for (Vehicle v : vehicles) {
            if (v.getVehicleId() == vehicleId) return v.getMakeModel() + " (" + displayId(v) + ")";
        }
        return String.format("V-%03d", vehicleId);
    }
}