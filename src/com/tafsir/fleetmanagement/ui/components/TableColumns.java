package com.tafsir.fleetmanagement.ui.components;

import java.util.function.Function;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableColumns {
	
	//Builds a column for any kind of row(vehicle now, trips later)
	//"property is the getter name without "get", first letter lowercase:
	//so getMakeModel() becomes "makeModel"
	
	public static <S> TableColumn<S, Object> of(String title, String property, double width) {
		TableColumn<S, Object> column = new TableColumn<>(title);
		column.setCellValueFactory(new PropertyValueFactory<>(property));
		column.setPrefWidth(width);
		return column;
	}
	
	// A column whose text is built by a function, e.g. 1 -> "V-001" or 32410 -> "32,410"
	public static <S> TableColumn<S, String> formatted(String title, Function<S, String> formatter, double width) {
		TableColumn<S, String> column = new TableColumn<>(title);
		column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(formatter.apply(cell.getValue())));
		column.setPrefWidth(width);
		return column;
		
	}

}
