package com.tafsir.fleetmanagement.ui.components;

import java.util.function.BooleanSupplier;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.stage.Window;

public class FormDialog {
	
	private final Dialog<ButtonType> dialog = new Dialog<>();
	private final ButtonType confirmType;
	
	public FormDialog(String title, String header, String confirmText, Node form, BooleanSupplier validator) {
		dialog.setTitle(title);
		dialog.setHeaderText(header);
		
		confirmType = new ButtonType(confirmText, ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(confirmType, ButtonType.CANCEL);
		dialog.getDialogPane().getStylesheets().add(Theme.stylesheet());
		dialog.getDialogPane().setContent(form);
		
		Node confirmButton = dialog.getDialogPane().lookupButton(confirmType);
		confirmButton.getStyleClass().add("primary-button");
		
		confirmButton.addEventFilter(ActionEvent.ACTION, event -> {
			if (!validator.getAsBoolean()) {
				event.consume();
				
				Platform.runLater(() -> dialog.getDialogPane().getScene().getWindow().sizeToScene());
			}
		});
	}
	
	// Shows the dialog and waits. Returns true only if the main button was clicked AND the check passed.
	public boolean ShowAndWait(Window owner) {
		dialog.initOwner(owner);
		return dialog.showAndWait().filter(button -> button == confirmType).isPresent();
	}

}
