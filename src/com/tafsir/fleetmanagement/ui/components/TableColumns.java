package com.tafsir.fleetmanagement.ui.components;

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

}
