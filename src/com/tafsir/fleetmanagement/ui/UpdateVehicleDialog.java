package com.tafsir.fleetmanagement.ui;

import com.tafsir.fleetmanagement.logic.Validator;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;
import com.tafsir.fleetmanagement.ui.components.FormDialog;
import com.tafsir.fleetmanagement.ui.components.LabeledField;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

public class UpdateVehicleDialog {
	
	
	//Changes the mileage and status of ONE existing vehicle. Like AddVehicleDialog it never touches the database
	// the caller reads the new values and saves through VehicleDAO
	private final Validator validator = new Validator();
	private final Vehicle vehicle;
	private final String idLabel;
	
	private final TextField mileageField = new TextField();
	private final ComboBox<VehicleStatus> statusBox = new ComboBox<>();
	private final LabeledField mileageRow = new LabeledField("Mileage", mileageField);
	private final LabeledField statusRow = new LabeledField("Status", statusBox);
	
	private int newMileage;
	private VehicleStatus newStatus;
	
	public UpdateVehicleDialog(Vehicle vehicle, String idLabel) {
		this.vehicle =vehicle;
		this.idLabel = idLabel;
		
	}
	
	//Show the dialog. Returns true if the user saved valid changes.
	public boolean showAndWait(Window owner) {
		FormDialog dialog = new FormDialog(
				"FMAS - Update Vehicle", "Update " + idLabel, "Save Changes", 
				buildForm(), this::validate);
		return dialog.ShowAndWait(owner);
	}
	
	public int getMileage() {
		return newMileage;
	}
	
	public VehicleStatus getStatus() {
		return newStatus;
	
	}
	
	private VBox buildForm() {
		Label summary = new Label(vehicle.getMakeModel() + "	|	" +  vehicle.getYear() + "	|	" + vehicle.getVehicleType());
		summary.getStyleClass().add("muted");
		
		mileageField.setText(String.format("%,d", vehicle.getMileage()));
		statusBox.getItems().addAll(VehicleStatus.values());
		statusBox.setValue(vehicle.getStatus());
		statusBox.setMaxWidth(Double.MAX_VALUE);
		
		VBox form = new VBox(14, summary, mileageRow, statusRow);
		form.setPadding(new Insets(18, 20, 8, 20));
		form.setPrefWidth(420);
		return form;
		
	}
		// Return true if the input is acceptable. Otherwise shows a red message under the problem field
		
		private boolean validate() {
			mileageRow.clearError();
			statusRow.clearError();
			boolean ok = true;
			
			
			String mileageText = mileageField.getText();
			if(!validator.requireFields(mileageText)) {
				mileageRow.setError("Mileage is required");
				ok = false;
			} else {
				try {
					int value = Integer.parseInt(mileageText.trim());
					if (!validator.validMileage(vehicle.getMileage(), value)) {
						mileageRow.setError("Mileage cannot be lower than the current " + String.format("%,d", vehicle.getMileage()));
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
			} else {
				newStatus =statusBox.getValue();
			}
			return ok;
				
		}		
			
}
