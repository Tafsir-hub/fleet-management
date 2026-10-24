package com.tafsir.fleetmanagement.app;

import com.tafsir.fleetmanagement.ui.VehicleScreen;
import com.tafsir.fleetmanagement.ui.components.AppShell;
import com.tafsir.fleetmanagement.ui.components.Dialogs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	@Override
	public void start(Stage stage) {
		AppShell shell = new AppShell(
				"FMAS \u2014 Vehicle Management",
				"Vehicles",
				new VehicleScreen(),
				name -> Dialogs.info("Coming Soon", name + " is built in a later slice."));
		
		Scene scene= new Scene(shell, 1100, 650);
		scene.getStylesheets().add(getClass().getResource("/fmas.css").toExternalForm());
		
		
		stage.setTitle("FMAS - Fleet Management & Analytics System");
		stage.setScene(scene);
		stage.show();
		
	}
	
	public static void main(String[] args) {
		launch (args);
	}

}
