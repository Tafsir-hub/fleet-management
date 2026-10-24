package com.tafsir.fleetmanagement.ui.components;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class HeaderBar extends HBox {
	
	private final Label titleLabel = new Label();
	
	public HeaderBar(String title) {
		
		getStyleClass().add("header-bar");
		titleLabel.getStyleClass().add("header-title");
		titleLabel.setText(title);
		getChildren().add(titleLabel);
		
	}
		public void setTitle(String title) {
			titleLabel.setText(title);
		
	}

}
