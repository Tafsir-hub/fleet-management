package com.tafsir.fleetmanagement.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class MessageBar extends HBox {
	
	private final Label label = new Label();
	
	public MessageBar() {
		getStyleClass().add("message-bar");
		getChildren().add(label);
		hide();
	}
	
	public void success(String text) {
		show(text, "message-success");
	}
	
	public void error(String text) {
		show(text, "message-error");
	}
	
	public void hide() {
		setVisible(false);
		setManaged(false);
	}
	
	private void show(String text, String styleClass) {
		label.setText(text);
		getStyleClass().removeAll("message-success", "message-error");
		getStyleClass().add(styleClass);
		setVisible(true);
		setManaged(true);
	}

}
