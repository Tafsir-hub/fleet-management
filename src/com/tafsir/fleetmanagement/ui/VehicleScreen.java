package com.tafsir.fleetmanagement.ui;



import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;
import com.tafsir.fleetmanagement.ui.components.AllChoiceBox;
import com.tafsir.fleetmanagement.ui.components.Dialogs;
import com.tafsir.fleetmanagement.ui.components.LabeledField;
import com.tafsir.fleetmanagement.ui.components.PrimaryButton;
import com.tafsir.fleetmanagement.ui.components.TabSwitch;
import com.tafsir.fleetmanagement.ui.components.TableColumns;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


public class VehicleScreen extends BorderPane {
	
	private static final String[] TYPES = 
		{"Van", "Pickup", "SUV", "Sports Car", "Box Truck", "Semi Tractor"};
	
	private final AllChoiceBox<VehicleStatus> statusFilter = new AllChoiceBox<>(VehicleStatus.values());
	private final AllChoiceBox<String> typeFilter = new AllChoiceBox<>(TYPES);

	private final TextField searchField = new TextField();
	private final VehicleDAO vehicleDAO = new VehicleDAO();
	private final TableView<Vehicle> table = new TableView<>();
	private final Label countLabel = new Label();
	private final BorderPane vehiclesPane = new BorderPane();
	private final StackPane driversPane = new StackPane();
	private final ObservableList<Vehicle> loadedVehicles = FXCollections.observableArrayList();
	private final FilteredList<Vehicle> shownVehicles = new FilteredList<>(loadedVehicles, v -> true);
	private final MessageBar messageBar = new MessageBar();
	
	
	public VehicleScreen() {
		
		setPadding(new Insets(18, 20, 14, 20));
		
		TabSwitch tabs = new TabSwitch(this::showTab, "Vehicles", "Drivers");
		setTop(tabs);
		BorderPane.setMargin(tabs, new Insets(0, 0, 16, 0));
		
		buildVehiclesPane();
		buildDriversPane();
		showTab("Vehicles");
		
		searchField.textProperty().addListener((obs, oldText, newText) -> applySearch());
		statusFilter.setOnAction(e -> loadVehicles());
		typeFilter.setOnAction(e -> loadVehicles());
		loadVehicles();
	}
	
	private void applySearch() {
		String text = searchField.getText() == null ? "": searchField.getText().trim().toLowerCase();
		shownVehicles.setPredicate(v -> text.isEmpty() || v.getMakeModel().toLowerCase().contains(text));
		countLabel.setText("Showing " + shownVehicles.size() + " vehicle(s)");
		
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
		
		searchField.setPromptText("Search by make/model...");
		searchField.setMaxWidth(Double.MAX_VALUE);
		statusFilter.setPrefWidth(170);
		typeFilter.setPrefWidth(170);
		
		LabeledField searchBox = new LabeledField("Search", searchField);
		HBox.setHgrow(searchBox, Priority.ALWAYS);
		
		PrimaryButton addButton = new PrimaryButton("+ Add Vehicle");
		addButton.setPrefWidth(200);
		addButton.setOnAction(e -> addVehicle());
		
		HBox filters = new HBox(16, searchBox,
				new LabeledField("Status", statusFilter),
				new LabeledField("Type", typeFilter),
				addButton);
		filters.setAlignment(Pos.BOTTOM_LEFT);
		filters.setPadding(new Insets(0, 0, 14, 0));
		
		table.getColumns().add(TableColumns.<Vehicle>formatted("ID", v -> displayId(v.getVehicleId()), 90));
        table.getColumns().add(TableColumns.of("Make / Model", "makeModel", 260));
        table.getColumns().add(TableColumns.of("Year", "year", 80));
        table.getColumns().add(TableColumns.of("Type", "vehicleType", 140));
        table.getColumns().add(TableColumns.<Vehicle>formatted("Mileage", v -> String.format("%,d", v.getMileage()), 120));
        table.getColumns().add(TableColumns.of("Status", "status", 120));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        table.setPlaceholder(new Label("No vehicles match these filters"));
        table.setItems(shownVehicles);
        table.setTooltip(new Tooltip("Double-click a row to update it"));
        table.setRowFactory(tv  -> {
        	TableRow<Vehicle> row = new TableRow<>(); row.setOnMouseClicked(event -> {
        		if (event.getClickCount() == 2 && !row.isEmpty()) {
        			updateVehicle(row.getItem());
        		}
        	});
        	return row;
        });
		
		countLabel.getStyleClass().add("muted");
		VBox bottom = new VBox(8, messageBar, countLabel);
		bottom.setPadding(new Insets(8, 0, 0, 0));
		
		vehiclesPane.setTop(filters);
		vehiclesPane.setCenter(table);
		vehiclesPane.setBottom(bottom);
		
	}
// Opens the update dialog, saves what changed through the DAO, then refreshes the table
private void updateVehicle(Vehicle vehicle) {
		UpdateVehicleDialog dialog = new UpdateVehicleDialog(vehicle, displayId(vehicle.getVehicleId()));
		if (!dialog.showAndWait(getScene().getWindow())) {
			return;
		}
		boolean mileageChanged = dialog.getMileage() != vehicle.getMileage();
		boolean statusChanged = dialog.getStatus()	!= vehicle.getStatus();
		if (!mileageChanged && !statusChanged) {
			return;
		}
		try {
			if (mileageChanged) {
				vehicleDAO.updateMileage(vehicle.getVehicleId(), dialog.getMileage());
			}
			if (statusChanged) {
				vehicleDAO.updateStatus(vehicle.getVehicleId(), dialog.getStatus());
			}
			loadVehicles();
			selectVehicle(vehicle.getVehicleId());
			messageBar.success("Updates " + displayId(vehicle.getVehicleId()));
		} catch (SQLException e){
			Dialogs.error("Could not update the vehicle", e.getMessage());
			
		}
		
	}

private static String displayId(int vehicleId) {
		return String.format("V-%03d", vehicleId);
		
	}

// Reads both filters, asks the DAO and shows the result in the table
	private void loadVehicles() {
		messageBar.hide();		
		try {
			List<Vehicle> vehicles = vehicleDAO.findAll(statusFilter.getSelection(), typeFilter.getSelection());
			loadedVehicles.setAll(vehicles);
			applySearch();
		}catch (SQLException e) {
			Dialogs.error("Could not load vehicles", e.getMessage());
		}
		
	}
	
	private void addVehicle() {
		Optional<Vehicle> entered = new AddVehicleDialog().showAndWait(getScene().getWindow());
		if (!entered.isPresent()) {
			return; // The user cancelled
		}
		try {
			int newId = vehicleDAO.addVehicle(entered.get());
			statusFilter.reset(); // This clears the filters so the the new row cannot be hidden
			typeFilter.reset();
			searchField.clear();
			loadVehicles();
			selectVehicle(newId);
			messageBar.success("Added " + displayId(newId) + ": " + entered.get().getMakeModel());
					
		} catch (SQLException e) {
			Dialogs.error("Could not add the vehicle", e.getMessage());
		}
	}
	
	private void selectVehicle(int vehicleId) {
		for (Vehicle v: shownVehicles) {
			if(v.getVehicleId() == vehicleId) {
				table.getSelectionModel().select(v);
				table.scrollTo(v);
				return;
			}
			
		}
		
	}

}
