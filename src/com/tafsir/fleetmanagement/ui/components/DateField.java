package com.tafsir.fleetmanagement.ui.components;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

// A date input: type MM/DD/YY or pick from the calendar.
public class DateField extends DatePicker {
	
	private static final DateTimeFormatter FORMAT = 
			DateTimeFormatter.ofPattern("MM/dd/uuuu").withResolverStyle(ResolverStyle.STRICT);
	
	public DateField() {
		setPromptText("MM/DD/YYYY");
		setMaxWidth(Double.MAX_VALUE);
		
		setConverter(new StringConverter<LocalDate>() {
			@Override
			public String toString(LocalDate date) {
				return date == null ? "" : date.format(FORMAT);
			}
			@Override
			public LocalDate fromString(String text) {
				if (text == null || text.trim().isEmpty()) {
					return null;
				}
				try {
					return LocalDate.parse(text.trim(), FORMAT);
				} catch (DateTimeParseException e) {
					return null;
				}
			}
		});
		
	}
	
	// The date in the box, or null if it is empty or not a valid date.
			public LocalDate getDate() {
				return getConverter().fromString(getEditor().getText());
			}
			
			// True if nothing has been typed or picked.
			public boolean isBlank() {
				String text = getEditor().getText();
				return text == null || text.trim().isEmpty();
			}
	
}
