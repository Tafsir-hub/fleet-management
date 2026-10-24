package com.tafsir.fleetmanagement.ui.components;



import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class LabeledField extends VBox {
	
	private final Label errorLabel = new Label();
	
	public LabeledField(String caption, Node input) {
		
		super(5);
		Label captionLabel = new Label(caption.toUpperCase());
		captionLabel.getStyleClass().add("field-caption");
		
		errorLabel.getStyleClass().add("field-error");
		errorLabel.setVisible(false);
		errorLabel.setManaged(false);
		
		getChildren().addAll(captionLabel, input, errorLabel);
		
	}
	
	public void setError(String message) {
		errorLabel.setText(message);
		errorLabel.setVisible(true);
		errorLabel.setManaged(true);
	}
	
	public void clearError() {
		errorLabel.setVisible(false);
		errorLabel.setManaged(false);
	}

}
