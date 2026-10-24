package com.tafsir.fleetmanagement.ui.components;

import javafx.scene.control.Button;

public class PrimaryButton extends Button {
	
	public PrimaryButton(String text) {
		super(text);
		getStyleClass().add("primary-button");
	}

}
