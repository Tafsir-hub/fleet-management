package com.tafsir.fleetmanagement.ui;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.tafsir.fleetmanagement.dao.DriverDAO;
import com.tafsir.fleetmanagement.model.Driver;
import com.tafsir.fleetmanagement.ui.components.Dialogs;
import com.tafsir.fleetmanagement.ui.components.PrimaryButton;
import com.tafsir.fleetmanagement.ui.components.TableColumns;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class DriverPane extends BorderPane{
	
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	
	private final DriverDAO driverDAO = new DriverDAO();
	private final TableView<Driver> table = new TableView<>();
	private final Label countLabel = new Label();
	
	
	public DriverPane() {
		PrimaryButton addButton = new PrimaryButton("+ Add Driver");
		addButton.setPrefWidth(200);
		addButton.setOnAction(e ->
				Dialogs.info("Coming soon", "The Add Driver form is built in the next step."));
		
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
		
		countLabel.getStyleClass().add("muted");
		BorderPane.setMargin(countLabel,new Insets(8, 0, 0, 0));
		
		setTop(top);
		setCenter(table);
		setBottom(countLabel);
		loadDrivers();		
		
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

}
