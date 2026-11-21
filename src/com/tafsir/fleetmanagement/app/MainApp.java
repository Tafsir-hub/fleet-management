package com.tafsir.fleetmanagement.app;

import com.tafsir.fleetmanagement.ui.FuelScreen;
import com.tafsir.fleetmanagement.ui.LoginScreen;
import com.tafsir.fleetmanagement.ui.MainMenuScreen;
import com.tafsir.fleetmanagement.ui.TripScreen;
import com.tafsir.fleetmanagement.ui.VehicleScreen;
import com.tafsir.fleetmanagement.ui.components.AppShell;
import com.tafsir.fleetmanagement.ui.components.Dialogs;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    private Scene scene;
    private AppShell shell;

    @Override
    public void start(Stage stage) {
        scene = new Scene(new LoginScreen(this::showMainMenu), 1100, 650);   //>> after a good login the Main Menu opens (TC-27)
        scene.getStylesheets().add(getClass().getResource("/fmas.css").toExternalForm());
        stage.setTitle("FMAS - Fleet Management & Analytics System");
        stage.setScene(scene);
        stage.show();
        
    }

    private void showMainMenu() {
        show("FMAS \u2014 Main Menu", "Main Menu", new MainMenuScreen(this::quickAction));   //>> "Main Menu" highlighted in the sidebar
    }

    private void showVehicles() {
        show("FMAS \u2014 Vehicle Management", "Vehicles", new VehicleScreen(this::syncSidebar));
    }
    
    private void showDrivers() {
    	VehicleScreen screen = new VehicleScreen(this::syncSidebar);
    	screen.showDriversTab();
    	show("FMAS \u2014 Vehicle Management", "Drivers", screen);
    }
    
    private void showTrips() {
    	show("FMAS — Trip Logging", "Trips", new TripScreen());
    }
    
    private void showFuel() {
        show("FMAS — Fuel & Maintenance", "Fuel", new FuelScreen(this::syncSidebar));
    }
    
    private void showMaintenance() {
        FuelScreen screen = new FuelScreen(this::syncSidebar);
        screen.showMaintenanceTab();
        show("FMAS \u2014 Fuel & Maintenance", "Maintenance", screen);
    }

    //>> The one place that builds a framed screen: header, sidebar and content.
    private void show(String title, String selectedNav, Node content) {
        shell = new AppShell(title, selectedNav, content, this::navigate);
        scene.setRoot(shell);
    }

    //>> Called by a screen when its tab changes (Drivers/Vehicles, Maintenance/Fuel): moves the sidebar highlight only
    private void syncSidebar(String name) {
        if (shell != null) shell.selectNav(name);
    }

    //>> Called by the sidebar with the name of the clicked item.
    private void navigate(String name) {
        switch (name) {
            case "Main Menu":
                showMainMenu();
                break;
            case "Vehicles":
                showVehicles();
                break;
            case "Drivers":
            	showDrivers();
            	break;
            case "Logout":
                scene.setRoot(new LoginScreen(this::showMainMenu));   //>> a NEW login screen: empty fields (TC-29)
                break;
            case "Trips":
            	showTrips();
            	break;
            case "Fuel":
                showFuel();
                break;
            case "Maintenance":
            	showMaintenance();
            	break;
            default:
                Dialogs.info("Coming later", name + " is built in a later slice.");
        }
    }

    private void quickAction(String action) {
        if ("Add Vehicle".equals(action)) {
            showVehicles();   //>> opens the Vehicles screen, where + Add Vehicle is one click away
        } else {
            Dialogs.info("Coming later", action + " is built in a later slice.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}