package com.tafsir.fleetmanagement.ui;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.dao.CostDAO;
import com.tafsir.fleetmanagement.dao.MpgDAO;
import com.tafsir.fleetmanagement.model.CostSummary;
import com.tafsir.fleetmanagement.model.MpgSummary;
import com.tafsir.fleetmanagement.model.Vehicle;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class CostReportPane extends BorderPane{
	
	// Filters 
	private final ComboBox<String> vehicleBox = new ComboBox<>();
	private final DateField fromField = new DateField();
	private final DateField toField = new DateField();
	private final Button runButton = new Button("Run Report");
	private final Button clearButton = new Button("Clear");
	
	// Results 
	private final TableView<CostSummary> table = new TableView<>();
	private final Label totalLabel = new Label();
	private final MessageBar messageBar = new MessageBar();
	
	private final VehicleDAO vehicleDAO = new VehicleDAO();
	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");
	private final CostDAO costDAO = new CostDAO();
	private final MpgDAO mpgDAO= new MpgDAO();
	private final Map<Integer,Double> milesByVehicle = new HashMap<>();
	
	
	private List<Vehicle> vehicles = new ArrayList<>();
	
	public CostReportPane() {
		setPadding(new Insets(24));
		setTop(buildForm());
		setCenter(buildResults());
		
		loadVehicles();
		clearButton.setOnAction(e -> clearFilters());
		runButton.setOnAction(e -> runReport());
	
	}
	
	private VBox buildForm() {
		vehicleBox.setPrefWidth(320);
		fromField.setPromptText("MM/DD/YYYY (Optional)");
		toField.setPromptText("MM/DD/YYYY (Optional)");
		
		GridPane form = new GridPane();
		form.setHgap(16);
		form.setVgap(12);
		for (int i = 0; i < 3; i++) {
			ColumnConstraints cc = new ColumnConstraints();
			cc.setPercentWidth(100.0 / 3);
			form.getColumnConstraints().add(cc);
		}
		form.add(field("VEHICLE", vehicleBox), 0, 0);
		form.add(field("FROM", fromField), 1, 0);
		form.add(field("TO", toField), 2, 0);
		
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);
		HBox buttonRow = new HBox(12, spacer, clearButton, runButton);
		buttonRow.setAlignment(Pos.CENTER_RIGHT);
		
		Label formTitle = new Label("OPERATING COST REPORT");
		formTitle.getStyleClass().add("section-title");
		Label hint = new Label("Fuel plus maintenance cost per vehicle. Leave the dates empty to include all dates.");
		hint.getStyleClass().add("muted");
		
		VBox formBox = new VBox(12, formTitle, hint, form, buttonRow);
		formBox.setPadding(new Insets(0, 0, 20, 0));
		return formBox;
	}
	
	private VBox buildResults() {
		table.getColumns().add(TableColumns.<CostSummary> formatted("Vehicle", r -> vehicleLabel(r.getVehicleId()), 300));
		table.getColumns().add(TableColumns.<CostSummary> formatted("Fuel", r -> String.format("$%,.2f", r.getFuelCost()), 130));
		table.getColumns().add(TableColumns.<CostSummary> formatted("Maintenance", r -> String.format("$%,.2f", r.getMaintenanceCost()), 130));
		table.getColumns().add(TableColumns.<CostSummary> formatted("Total", r -> String.format("$%,.2f", r.getTotal()), 130));
		table.getColumns().add(TableColumns.<CostSummary> formatted("Cost per mile", r -> perMile(r.getTotal(), milesByVehicle.get(r.getVehicleId())), 130));
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
		table.setPlaceholder(new Label("Choose a vehicle and dates, then click Run Report"));
		
		Label resultsTitle = new Label("COST BY VEHICLE");
		resultsTitle.getStyleClass().add("section-title");
		
		totalLabel.getStyleClass().add("muted");
		VBox footer = new VBox(8, messageBar, totalLabel);
		footer.setPadding(new Insets(8, 0, 0, 0));
		
		VBox results = new VBox(8, resultsTitle, table, footer);
		VBox.setVgrow(table, Priority.ALWAYS);
		return results;
	}
	
	//---------Actions--------------
	
	private void clearFilters() {
		vehicleBox.getSelectionModel().selectFirst();
		fromField.getEditor().clear();
		fromField.setValue(null);
		toField.getEditor().clear();
		toField.setValue(null);
		table.getItems().clear();
		totalLabel.setText("");
		messageBar.hide();
	}
	
	private void runReport() {
		LocalDate from = fromField.getDate();
		LocalDate to = toField.getDate();

		// Checks in field order; the first problem is shown. Empty dates are fine (they mean all dates).
		if (!fromField.isBlank() && from == null) { fail("Enter a valid From date, like 11/01/2026", fromField); return; }
		if (!toField.isBlank() && to == null) { fail("Enter a valid To date, like 11/30/2026", toField); return; }
		if (from != null && to != null && from.isAfter(to)) { fail("The From date cannot be after the To date", fromField); return; }

		// First item is "All vehicles", so the vehicle list is shifted by one
		int pick = vehicleBox.getSelectionModel().getSelectedIndex();
		Integer vehicleId = pick <= 0 ? null : vehicles.get(pick - 1).getVehicleId();

		try {
			List<CostSummary> rows = costDAO.findCosts(vehicleId, from, to);

			// Trip miles for the same vehicle and dates, kept by vehicle for the Cost per mile column
			milesByVehicle.clear();
			double miles = 0;
			for (MpgSummary m : mpgDAO.findMpg(vehicleId, from, to)) {
				milesByVehicle.put(m.getVehicleId(), m.getMiles());
				miles += m.getMiles();
			}
			table.setItems(FXCollections.observableArrayList(rows));
			double fuel = 0;
			double maintenance = 0;
			for (CostSummary r : rows) {
				fuel += r.getFuelCost();
				maintenance += r.getMaintenanceCost();
			}
			double total = Math.round((fuel + maintenance) * 100.0) / 100.0;

			String who = vehicleId == null ? "all vehicles" : vehicleLabel(vehicleId);
			String period;
			if (from != null && to != null) period = from.format(DATE_FMT) + " to " + to.format(DATE_FMT);
			else if (from != null) period = "from " + from.format(DATE_FMT);
			else if (to != null) period = "up to " + to.format(DATE_FMT);
			else period = "all dates";

			totalLabel.setText(String.format("Total for %s, %s: $%,.2f   (fuel $%,.2f + maintenance $%,.2f)   Cost per mile: %s",
					who, period, total, fuel, maintenance, perMile(total, miles)));
			messageBar.hide();
		} catch (SQLException e) {
			e.printStackTrace();
			messageBar.error("Could not run the report");
		}
	}

	// Red message plus the cursor on the field that needs fixing
	private void fail(String text, Node field) {
		messageBar.error(text);
		field.requestFocus();
	}
    
	//---------Helpers---------------
	
	// Small label above a control, like the other forms
	private VBox field(String caption, Node control) {
		if (control instanceof Region) {
			((Region) control).setMaxWidth(Double.MAX_VALUE);
		}
		return new VBox(4, new Label(caption), control);
		
	}
	
	// Cost divided by trip miles; no miles means no cost per mile
	private String perMile(double cost, Double miles) {
		if (miles == null || miles <= 0) return "\u2014";
		return String.format("$%.2f", cost / miles);
	}
	
	// Every vehicle (Including Retired ones): their past cost still count
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
		for (Vehicle v: vehicles) labels.add(vehicleLabel(v.getVehicleId()));
		vehicleBox.setItems(FXCollections.observableArrayList(labels));
		vehicleBox.getSelectionModel().selectFirst();
	}
		
		private String vehicleLabel(int vehicleId) {
			for (Vehicle v : vehicles) {
				if(v.getVehicleId() == vehicleId)
					return v.getMakeModel() + " (" + String.format("V-%03d", vehicleId) + ")";
			}
			return String.format("V-%03d", vehicleId);
		}

}
