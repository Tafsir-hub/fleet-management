package com.tafsir.fleetmanagement.ui;

import java.time.format.DateTimeFormatter;

import com.tafsir.fleetmanagement.model.Driver;
import com.tafsir.fleetmanagement.model.DriverStatus;
import com.tafsir.fleetmanagement.ui.components.FormDialog;
import com.tafsir.fleetmanagement.ui.components.LabeledField;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

// Changes the status of ONE driver (UC-06). It never touches the database: the screen reads the answer.
//>> Same shape as UpdateVehicleDialog: FormDialog as the shell, a getter for the accepted value.
public class UpdateDriverDialog {

    private final Driver driver;
    private final String idLabel;   //>> "D-001", built by the screen so the ID format lives in one place

    private final ComboBox<DriverStatus> statusBox = new ComboBox<>();   //>> not editable: only Active or Inactive can be picked
    private final LabeledField statusRow = new LabeledField("Status", statusBox);

    private DriverStatus newStatus;   //>> filled in by validate() once the input is accepted

    public UpdateDriverDialog(Driver driver, String idLabel) {
        this.driver = driver;
        this.idLabel = idLabel;
    }

    // Shows the dialog. Returns true if the user clicked Save Changes.
    public boolean showAndWait(Window owner) {
        FormDialog dialog = new FormDialog(
                "FMAS - Update Driver", "Update " + idLabel, "Save Changes",
                buildForm(), this::validate);
        return dialog.ShowAndWait(owner);
    }

    public DriverStatus getStatus() {   //>> the screen reads the accepted value after showAndWait returns true
        return newStatus;
    }

    private VBox buildForm() {
        //>> Read-only reminder of WHICH driver this is. A Label cannot be edited.
        Label summary = new Label(driver.getFullName() + "  |  " + driver.getLicenseType()
                + "  |  hired " + driver.getHireDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        summary.getStyleClass().add("muted");

        statusBox.getItems().addAll(DriverStatus.values());   //>> exactly Active and Inactive
        statusBox.setValue(driver.getStatus());               //>> preselect the current status
        statusBox.setMaxWidth(Double.MAX_VALUE);

        VBox form = new VBox(14, summary, statusRow);
        form.setPadding(new Insets(18, 20, 8, 20));
        form.setPrefWidth(420);
        return form;
    }

    private boolean validate() {
        statusRow.clearError();
        newStatus = statusBox.getValue();
        if (newStatus == null) {   //>> cannot happen with a preselected dropdown; this only guards against it
            statusRow.setError("Choose a status");
            return false;
        }
        return true;
    }
}