package com.tafsir.fleetmanagement.ui;

import java.util.Optional;

import com.tafsir.fleetmanagement.logic.Validator;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;
import com.tafsir.fleetmanagement.model.VehicleTypes;
import com.tafsir.fleetmanagement.ui.components.FormDialog;
import com.tafsir.fleetmanagement.ui.components.LabeledField;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

// Collects and checks the details of a new vehicle. It never touches the database
// The caller receives the Vehicle and decides what to do with it.
public class AddVehicleDialog {
	
	private final Validator validator = new Validator();
	
	private final TextField makeModelField = new TextField();
	private final TextField yearField = new TextField();
	private final ComboBox<String> typeBox = new ComboBox<>();
	private final TextField mileageField = new TextField();
	private final ComboBox<VehicleStatus> statusBox = new ComboBox<>();
	
	private final LabeledField  makeModelRow = new LabeledField("Make / Model", makeModelField);
	private final LabeledField  yearRow = new LabeledField("Year", yearField);
	private final LabeledField  typeRow = new LabeledField("Type", typeBox);
	private final LabeledField  mileageRow = new LabeledField("Mileage", mileageField);
	private final LabeledField  statusRow = new LabeledField("Status", statusBox);
	
	private Vehicle result;
	
	//Shows the dialog. Return the new vehicle, or empty if the user cancelled
	public Optional<Vehicle> showAndWait(Window owner) {
		FormDialog dialog = new FormDialog(
				"FMAS - Add Vehicle", "Add a vehicle to the fleet", "Add Vehicle", 
				buildForm(), this::validate);
		return dialog.ShowAndWait(owner) ? Optional.ofNullable(result) : Optional.empty();
	}

	private VBox buildForm() {
		makeModelField.setPromptText("e.g. Ford Transit");
		yearField.setPromptText("e.g. 2022");
		typeBox.getItems().addAll(VehicleTypes.ALL);
		typeBox.setPromptText("Select a Type");
		typeBox.setMaxWidth(Double.MAX_VALUE);
		mileageField.setPromptText("e.g 12000");
		statusBox.getItems().addAll(VehicleStatus.values());
		statusBox.setValue(VehicleStatus.ACTIVE);
		statusBox.setMaxWidth(Double.MAX_VALUE);
		
		VBox form = new VBox(14, makeModelRow, yearRow, typeRow, mileageRow, statusRow);
		form.setPadding(new Insets(18, 20, 8, 20));
		form.setPrefWidth(420);
		return form;
	}

	//Returns true if everything is acceptable. Otherwise shows a red message
	// under each problem field and returns false
	private boolean validate() {
		makeModelRow.clearError();
		yearRow.clearError();
		typeRow.clearError();
		mileageRow.clearError();
		statusRow.clearError();
		boolean ok = true;
		
		String makeModel = makeModelField.getText();
		if(!validator.requireFields(makeModel)) {
			makeModelRow.setError("Make / model is required");
			ok = false;
		}
		int year = 0;
		String yearText = yearField.getText();
		if(!validator.requireFields(yearText)) {
			yearRow.setError("Year is required");
			ok = false;
		} else { 
			try {
				year = Integer.parseInt(yearText.trim());
			} catch (NumberFormatException e) {
				yearRow.setError("Year must be a whole number");
				ok = false;
			}
		}
		
		String type = typeBox.getValue();
		if(!validator.requireFields(type)) {
			typeRow.setError("Choose a type");
			ok = false;
		}
		
		int mileage = 0;
		String mileageText = mileageField.getText();
		if(!validator.requireFields(mileageText)) {
			mileageRow.setError("Mileage is required");
			ok = false;
		} else {
			try {
				mileage = Integer.parseInt(mileageText.trim().replace(",", ""));
				if (!validator.validMileage(0, mileage)) {
					mileageRow.setError("Mileage cannot be negative");
					ok = false;
				}	
			} catch (NumberFormatException e) {
				mileageRow.setError("Mileage must be a whole number");
				ok = false;
			}
		}
		
		if (!validator.validStatus(statusBox.getValue())) {
			statusRow.setError("Choose a status");
			ok = false;
		}
			
		if (ok) {
				result = new Vehicle(0, makeModel.trim(), year, type, mileage, statusBox.getValue());
		}
		return ok;
		
	}
	
}
