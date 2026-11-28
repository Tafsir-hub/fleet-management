package com.tafsir.fleetmanagement.ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.dao.MaintenanceDueDAO;
import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.logic.MaintenanceRule;
import com.tafsir.fleetmanagement.model.DueVehicle;
import com.tafsir.fleetmanagement.model.ServiceStatus;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;
import com.tafsir.fleetmanagement.ui.components.TableColumns;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

//>> Maintenance-due report (UC-12): the vehicles that are due for service today, with the reason.
//>> The rule itself lives in MaintenanceRule; this screen only shows the result. The list loads when the report opens.
public class MaintenanceDuePane extends BorderPane {

	private final ComboBox<String> vehicleBox = new ComboBox<>();
	private final Button runButton = new Button("Run Report");

	private final TableView<DueVehicle> table = new TableView<>();
	private final Label summaryLabel = new Label();
	private final MessageBar messageBar = new MessageBar();

	private final VehicleDAO vehicleDAO = new VehicleDAO();
	private final MaintenanceDueDAO dueDAO = new MaintenanceDueDAO();
	private final MaintenanceRule rule = new MaintenanceRule();
	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private List<Vehicle> vehicles = new ArrayList<>();

	public MaintenanceDuePane() {
		setPadding(new Insets(24));
		setTop(buildForm());
		setCenter(buildResults());

		loadVehicles();
		runButton.setOnAction(e -> runReport());
		runReport();   // the list is useful straight away, so it loads when the report opens
	}

	private VBox buildForm() {
		vehicleBox.setPrefWidth(320);
		VBox vehicleField = new VBox(4, new Label("VEHICLE"), vehicleBox);

		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		HBox row = new HBox(16, vehicleField, spacer, runButton);
		row.setAlignment(Pos.BOTTOM_LEFT);

		Label formTitle = new Label("MAINTENANCE DUE REPORT");
		formTitle.getStyleClass().add("section-title");
		Label hint = new Label(String.format(
				"A vehicle is due when it has gone %,d miles or %d days since its last service, or was never serviced. "
				+ "Retired vehicles are not listed.", MaintenanceRule.MILES_LIMIT, MaintenanceRule.DAYS_LIMIT));
		hint.getStyleClass().add("muted");
		hint.setWrapText(true);

		VBox formBox = new VBox(12, formTitle, hint, row);
		formBox.setPadding(new Insets(0, 0, 20, 0));
		return formBox;
	}

	private VBox buildResults() {
		table.getColumns().add(TableColumns.<DueVehicle> formatted("Vehicle", r -> vehicleLabel(r.getVehicleId()), 280));
		table.getColumns().add(TableColumns.<DueVehicle> formatted("Last service",
				r -> r.getLastServiceDate() == null ? "Never" : r.getLastServiceDate().format(DATE_FMT), 120));
		table.getColumns().add(TableColumns.<DueVehicle> formatted("Miles since",
				r -> r.getMilesSince() == null ? "—" : String.format("%,d", r.getMilesSince()), 110));
		table.getColumns().add(TableColumns.<DueVehicle> formatted("Days since",
				r -> r.getDaySince() == null ? "—" : String.valueOf(r.getDaySince()), 110));
		table.getColumns().add(TableColumns.<DueVehicle> formatted("Why it is due", DueVehicle::getReason, 220));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		table.setPlaceholder(new Label("No vehicle is due for service"));

		Label resultsTitle = new Label("DUE FOR SERVICE");
		resultsTitle.getStyleClass().add("section-title");

		summaryLabel.getStyleClass().add("muted");
		VBox footer = new VBox(8, messageBar, summaryLabel);
		footer.setPadding(new Insets(8, 0, 0, 0));

		VBox results = new VBox(8, resultsTitle, table, footer);
		VBox.setVgrow(table, Priority.ALWAYS);
		return results;
	}

	//---------Actions--------------

	private void runReport() {
		// First item is "All vehicles", so the vehicle list is shifted by one
		int pick = vehicleBox.getSelectionModel().getSelectedIndex();
		Integer vehicleId = pick <= 0 ? null : vehicles.get(pick - 1).getVehicleId();

		try {
			List<ServiceStatus> statuses = new ArrayList<>();
			for (ServiceStatus s : dueDAO.findServiceStatus()) {
				if (vehicleId == null || s.getVehicleId() == vehicleId) statuses.add(s);
			}

			List<DueVehicle> due = rule.findDue(statuses, LocalDate.now());
			table.setItems(FXCollections.observableArrayList(due));

			int listed = 0;   // vehicles the rule looks at: everything except Retired
			boolean retiredPick = false;
			for (ServiceStatus s : statuses) {
				if (s.getStatus() == VehicleStatus.RETIRED) retiredPick = true;
				else listed++;
			}

			if (vehicleId != null && listed == 0 && retiredPick) {
				summaryLabel.setText(vehicleLabel(vehicleId) + " is retired, so it is not listed.");
			} else if (vehicleId != null) {
				summaryLabel.setText(vehicleLabel(vehicleId) + (due.isEmpty()
						? " is not due for service."
						: " is due for service: " + due.get(0).getReason() + "."));
			} else {
				summaryLabel.setText(String.format("%d of %d vehicles are due for service today (%s).",
						due.size(), listed, LocalDate.now().format(DATE_FMT)));
			}
			messageBar.hide();
		} catch (SQLException e) {
			e.printStackTrace();
			messageBar.error("Could not run the report");
		}
	}

	//---------Helpers---------------

	// Every vehicle, so a vehicle that is not due can still be picked and explained
	private void loadVehicles() {
		try {
			vehicles = vehicleDAO.findAll(null, null);
		} catch (SQLException e) {
			e.printStackTrace();
			vehicles = new ArrayList<>();
			messageBar.error("Could not load the vehicles");
		}
		List<String> labels = new ArrayList<>();
		labels.add("All vehicles");
		for (Vehicle v : vehicles) labels.add(vehicleLabel(v.getVehicleId()));
		vehicleBox.setItems(FXCollections.observableArrayList(labels));
		vehicleBox.getSelectionModel().selectFirst();
	}

	private String vehicleLabel(int vehicleId) {
		for (Vehicle v : vehicles) {
			if (v.getVehicleId() == vehicleId)
				return v.getMakeModel() + " (" + String.format("V-%03d", vehicleId) + ")";
		}
		return String.format("V-%03d", vehicleId);
	}
}