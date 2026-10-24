package com.tafsir.fleetmanagement.ui.components;

import javafx.scene.control.Alert;

public final class Dialogs {
	
	private Dialogs() {
	}
	
	public static void error(String header, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle("FMAS");
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	public static void info(String header, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("FMAS");
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.showAndWait();
	}

}
