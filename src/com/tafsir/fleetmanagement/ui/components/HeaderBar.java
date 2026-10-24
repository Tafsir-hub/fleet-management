package com.tafsir.fleetmanagement.ui.components;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class HeaderBar extends HBox {
	
	private final Label titleLabel = new Label();
	
	public HeaderBar(String title) {
		titleLabel.setText(title);
		titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px; -fx-font-weight: bold;");
		getChildren().add(titleLabel);
		setPadding(new Insets(14, 18,14,18));
		setStyle("fx_background-color: " + Theme.NAVY + ";");
	}
		public void setTitle(String title) {
			titleLabel.setText(title);
		
	}

}
