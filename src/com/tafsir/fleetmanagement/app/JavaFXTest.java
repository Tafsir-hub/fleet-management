package com.tafsir.fleetmanagement.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class JavaFXTest extends Application{

	@Override
	public void start(Stage stage)  {
		Label label = new Label("JavaFX works: FMAS");
		stage.setScene(new Scene(new StackPane(label), 400, 200));
		stage.setTitle("FMAS - JavaFX test");
		stage.show();
		
	}
	
	public static void main(String[] args) {
		launch (args);

		
	}

}
