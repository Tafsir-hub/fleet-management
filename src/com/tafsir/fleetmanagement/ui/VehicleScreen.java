package com.tafsir.fleetmanagement.ui;


import com.tafsir.fleetmanagement.ui.components.HeaderBar;

import javafx.scene.layout.BorderPane;

public class VehicleScreen extends BorderPane {
	
	public VehicleScreen() {
		setTop(new HeaderBar("FMAS	|	Vehicles"));
		
	}

}
