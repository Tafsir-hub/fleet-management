package com.tafsir.fleetmanagement.ui;

import java.time.LocalDate;
import java.util.Optional;

import com.tafsir.fleetmanagement.logic.Validator;
import com.tafsir.fleetmanagement.model.DriverStatus;
import com.tafsir.fleetmanagement.model.LicenseTypes;
import com.tafsir.fleetmanagement.model.Driver;
import com.tafsir.fleetmanagement.ui.components.FormDialog;
import com.tafsir.fleetmanagement.ui.components.LabeledField;
import com.tafsir.fleetmanagement.ui.components.DateField;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

// Collects and checks the details of a new Driver. It never touches the database
// The caller receives the Driver and decides what to do with it.
public class AddDriverDialog {
	
	private final Validator validator = new Validator();
	
	private final TextField firstNameField = new TextField();
	private final TextField lastNameField = new TextField();
	private final ComboBox<String> licenseBox = new ComboBox<>();
	private final DateField hireDateField = new DateField();
	
	
	
	private final LabeledField  firstNameRow = new LabeledField("First name", firstNameField);
	private final LabeledField  lastNameRow = new LabeledField("Last name", lastNameField);
	private final LabeledField  licenseRow = new LabeledField("License type", licenseBox);
	private final LabeledField  hireDateRow = new LabeledField("Hire date", hireDateField);
	
	
	private Driver result;
	
	//Shows the dialog. Return the new vehicle, or empty if the user cancelled
	public Optional<Driver> showAndWait(Window owner) {
		FormDialog dialog = new FormDialog(
				"FMAS - Add Driver", "Add a driver to the fleet", "Add Driver", 
				buildForm(), this::validate);
		return dialog.ShowAndWait(owner) ? Optional.ofNullable(result) : Optional.empty();
	}

	private VBox buildForm() {
		firstNameField.setPromptText("e.g. Maria");
		lastNameField.setPromptText("e.g. Santos");
		licenseBox.getItems().addAll(LicenseTypes.ALL);
		licenseBox.setPromptText("Select a type");
		licenseBox.setMaxWidth(Double.MAX_VALUE);
		
		
		VBox form = new VBox(14, firstNameRow, lastNameRow, licenseRow, hireDateRow);
		form.setPadding(new Insets(18, 20, 8, 20));
		form.setPrefWidth(420);
		return form;
	}

	//Returns true if everything is acceptable. Otherwise shows a red message
	// under each problem field and returns false, and the dialog stays open
	private boolean validate() {
		firstNameRow.clearError();
		lastNameRow.clearError();
		licenseRow.clearError();
		hireDateRow.clearError();
		boolean ok = true;
		
		
		if(!validator.requireFields(firstNameField.getText())) {
			firstNameRow.setError("First Name is required");
			ok = false;
		}
		if(!validator.requireFields(lastNameField.getText())) {
			lastNameRow.setError("Last Name is required");
			ok = false;
		
		}
		
		if(!validator.requireFields(licenseBox.getValue())) {
			licenseRow.setError("Choose a license type");
			ok = false;
		}
		
		LocalDate hireDate = hireDateField.getDate();
		if (hireDate == null) {
			// Two different problems, two different messages: nothing entered, or text that is not a real date
			hireDateRow.setError(hireDateField.isBlank()
					? "Hire date is required"
					: "Enter a valid date, like 03/11/2029");
			ok = false;
		}
			
		if (ok) {
				result = new Driver(0, firstNameField.getText().trim(), lastNameField.getText().trim(), 
						licenseBox.getValue(), hireDate, DriverStatus.ACTIVE);
		}
		return ok;
		
	}
	
}
