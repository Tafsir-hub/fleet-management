package com.tafsir.fleetmanagement.ui.components;


import javafx.scene.control.ComboBox;

//A dropdown with an "All" option first. Nothing can be typed in it.
public class AllChoiceBox<T> extends ComboBox<Object> {
	
	private static final String ALL = "All";
	
	public AllChoiceBox(T[] options) {
		getItems().add(ALL);
		getItems().addAll(options);
		setValue(ALL);
	}
	
	@SuppressWarnings("unchecked")
	public T getSelection() {
		Object value = getValue();
		return ALL.equals(value) ? null : (T) value;
	}
	
	public void reset() {
		setValue(ALL);
	}

}
