package com.tafsir.fleetmanagement.app;

import com.tafsir.fleetmanagement.ui.VehicleScreen;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
	
	@Override
	public void start(Stage stage) {
		
		//Slice 1: open the Vehicles screen directly. Login in subsequent steps
		stage.setTitle("FMAS - Fleet Management & Analytics System");
		stage.setScene(new Scene(new VehicleScreen(), 900, 560));
		stage.show();
		
	}
	
	public static void main(String[] args) {
		launch (args);
	}

}
