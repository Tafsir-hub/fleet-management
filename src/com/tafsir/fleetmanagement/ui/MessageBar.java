package com.tafsir.fleetmanagement.ui;

import javafx.animation.PauseTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class MessageBar extends HBox {

	private static final double SUCCESS_SECONDS = 8;
	private static final double ERROR_SECONDS = 10;

	private final Label label = new Label();
	private final PauseTransition hideTimer = new PauseTransition();

	public MessageBar() {
		getStyleClass().add("message-bar");
		getChildren().add(label);
		hideTimer.setOnFinished(e -> hide());
		hide();
	}

	public void success(String text) {
		show(text, "message-success", SUCCESS_SECONDS);
	}

	public void error(String text) {
		show(text, "message-error", ERROR_SECONDS);
	}

	public void hide() {
		hideTimer.stop();
		setVisible(false);
		setManaged(false);
	}

	private void show(String text, String styleClass, double seconds) {
		label.setText(text);
		getStyleClass().removeAll("message-success", "message-error");
		getStyleClass().add(styleClass);
		setVisible(true);
		setManaged(true);
		hideTimer.setDuration(Duration.seconds(seconds));
		hideTimer.playFromStart();   // a new message restarts the timer
	}
}