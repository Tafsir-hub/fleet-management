package com.tafsir.fleetmanagement.ui;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.dao.FuelDAO;
import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.model.FuelEntry;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.ui.components.TabSwitch;
import com.tafsir.fleetmanagement.ui.components.TableColumns;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FuelScreen extends BorderPane {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    private final FuelDAO fuelDAO = new FuelDAO();
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    private final ComboBox<String> vehicleFilter = new ComboBox<>();
    private final TableView<FuelEntry> table = new TableView<>();
    private final Label historyTitle = new Label();
    private final Label countLabel = new Label();
    private final MessageBar messageBar = new MessageBar();
    private final BorderPane fuelPane = new BorderPane();
    private final BorderPane maintenancePane = new BorderPane();

    private List<Vehicle> vehicles = new ArrayList<>();

    public FuelScreen() {
        setPadding(new Insets(18, 20, 14, 20));

        TabSwitch tabs = new TabSwitch(this::showTab, "Fuel", "Maintenance");
        setTop(tabs);
        BorderPane.setMargin(tabs, new Insets(0, 0, 16, 0));

        buildFuelPane();
        buildMaintenancePane();
        showTab("Fuel");

        loadVehicles();
        vehicleFilter.valueProperty().addListener((obs, old, v) -> loadEntries());
        loadEntries();
    }

    private void showTab(String tab) {
        setCenter("Maintenance".equals(tab) ? maintenancePane : fuelPane);
    }

    private void buildFuelPane() {
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

        fuelPane.setTop(header);
        fuelPane.setCenter(table);
        fuelPane.setBottom(bottom);
    }

    private void buildMaintenancePane() {
        Label soon = new Label("Maintenance records are built in a later slice.");
        soon.getStyleClass().add("muted");
        maintenancePane.setCenter(soon);
    }

    // Fills the vehicle filter: "All vehicles" first, then every vehicle (history includes inactive ones)
    private void loadVehicles() {
        try {
            vehicles = vehicleDAO.findAll(null, null);
        } catch (SQLException e) {
            e.printStackTrace();
            vehicles = new ArrayList<>();
        }
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