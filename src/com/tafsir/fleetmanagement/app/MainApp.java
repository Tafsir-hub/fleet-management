package com.tafsir.fleetmanagement.app;

import com.tafsir.fleetmanagement.ui.LoginScreen;
import com.tafsir.fleetmanagement.ui.VehicleScreen;
import com.tafsir.fleetmanagement.ui.components.AppShell;
import com.tafsir.fleetmanagement.ui.components.Dialogs;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Scene scene; 

    @Override
    public void start(Stage stage) {
        scene = new Scene(new LoginScreen(this::showMain), 1100, 650);   
        scene.getStylesheets().add(getClass().getResource("/fmas.css").toExternalForm());
        stage.setTitle("FMAS - Fleet Management & Analytics System");
        stage.setScene(scene);
        stage.show();
    }

    
    private void showMain() {
        scene.setRoot(new AppShell("FMAS \u2014 Vehicle Management", "Vehicles",
                new VehicleScreen(), this::navigate));
    }

    
    private void navigate(String name) {
        if ("Logout".equals(name)) {
            
            scene.setRoot(new LoginScreen(this::showMain));
        } else {
            Dialogs.info("Coming later", name + " is built in a later slice.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}