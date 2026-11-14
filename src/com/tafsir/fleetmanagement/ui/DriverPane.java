package com.tafsir.fleetmanagement.ui;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.tafsir.fleetmanagement.dao.DriverDAO;
import com.tafsir.fleetmanagement.model.Driver;
import com.tafsir.fleetmanagement.model.DriverStatus;
import com.tafsir.fleetmanagement.ui.components.Dialogs;
import com.tafsir.fleetmanagement.ui.components.PrimaryButton;
import com.tafsir.fleetmanagement.ui.components.TableColumns;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// The content of the Drivers tab: the list of drivers and the + Add button.

public class DriverPane extends BorderPane{
	
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	
	private final DriverDAO driverDAO = new DriverDAO();
	private final TableView<Driver> table = new TableView<>();
	private final Label countLabel = new Label();
	private final MessageBar messageBar = new MessageBar();
	
	
	public DriverPane() {
		PrimaryButton addButton = new PrimaryButton("+ Add Driver");
		addButton.setPrefWidth(200);
		addButton.setOnAction(e -> addDriver());
		
		HBox top = new HBox(addButton);
		top.setAlignment(Pos.CENTER_RIGHT);
		top.setPadding(new Insets(0, 0, 14, 0));
		
		table.getColumns().add(TableColumns.<Driver>formatted("ID", d -> String.format("D-%03d", d.getDriverId()), 90));
		table.getColumns().add(TableColumns.<Driver>formatted("Name", Driver::getFullName, 240));
		table.getColumns().add(TableColumns.of("License", "licenseType", 130));
		table.getColumns().add(TableColumns.<Driver>formatted("Hire Date", d -> d.getHireDate().format(DATE_FORMAT), 130));
		table.getColumns().add(TableColumns.of("Status", "status", 120)); 
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		table.setPlaceholder(new Label("No drivers yet"));
		table.setRowFactory(tv -> {
			TableRow<Driver> row = new TableRow<>();
			row.setOnMouseClicked(e -> {
				if(e.getClickCount()== 2 && !row.isEmpty()) {
					updateDriver(row.getItem());
				}
			});
			return row;
		});
		
		countLabel.getStyleClass().add("muted");
		VBox bottom = new VBox(8, messageBar, countLabel);
		BorderPane.setMargin(countLabel,new Insets(8, 0, 0, 0));
		
		setTop(top);
		setCenter(table);
		setBottom(bottom);
		loadDrivers();		
		
	}
	
	// Opens the dialog, saves the driver through the DAO, then shows it in the table.
	private void addDriver() {
		Optional<Driver> entered = new AddDriverDialog().showAndWait(getScene().getWindow());
		if(!entered.isPresent()) {
			return;
		}
		try {
			int newId = driverDAO.addDriver(entered.get());
			loadDrivers();
			selectDriver(newId);
			messageBar.success("Added " + displayId(newId) + ": " + entered.get().getFullName());
		} catch (SQLException e) {
			Dialogs.error("could not add the driver" , e.getMessage());
		}
	}
	
	private void updateDriver(Driver driver) {
		UpdateDriverDialog dialog = new UpdateDriverDialog(driver, displayId(driver.getDriverId()));
		if (!dialog.showAndWait(getScene().getWindow())) {
			return; // the user cancelled
		}
		DriverStatus newStatus = dialog.getStatus();
		if (newStatus == driver.getStatus()) {
			return; 
		}
		 try {
				if (driverDAO.updateStatus(driver.getDriverId(), newStatus)) {
					loadDrivers();
					selectDriver(driver.getDriverId());
					messageBar.success(displayId(driver.getDriverId())+ ": " + driver.getFirstName()
					+ " is now " + newStatus.getLabel()); 
				} else {
					Dialogs.error("Could not update the driver", " No driver with that ID was found");
				}
			} catch (SQLException e) {
				Dialogs.error("Could not update the driver", e.getMessage());
			}
	}

	// Asks the DAO for every driver and shows them in the table.
	private void loadDrivers() {
		try {
			List<Driver> drivers = driverDAO.findAll();
			table.setItems(FXCollections.observableArrayList(drivers));
			countLabel.setText("Showing " + drivers.size() + " driver(s)");
		}catch (SQLException e) {
			Dialogs.error("Could not load drivers", e.getMessage());
		}
		
	}
	
	private void selectDriver(int driverId) {
		for (Driver d: table.getItems()) {
			if (d.getDriverId() == driverId) {
				table.getSelectionModel().select(d);
				table.scrollTo(d);
				return;
			}
		}
	}
	
	//D-001 style. built in one place: the table column and the success message both use it
	private static String displayId(int driverId) {
        return String.format("D-%03d", driverId);
    }

}
