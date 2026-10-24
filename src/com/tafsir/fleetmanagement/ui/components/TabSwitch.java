package com.tafsir.fleetmanagement.ui.components;

import java.util.function.Consumer;

import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class TabSwitch extends HBox {
	
	public TabSwitch(Consumer<String> onSelect, String... tabs) {
		super(10);
		ToggleGroup group = new ToggleGroup();
		for (String tab: tabs) {
			ToggleButton button = new ToggleButton(tab);
			button.setToggleGroup(group);
			button.setPrefWidth(160);
			button.getStyleClass().add("tab-button");
			getChildren().add(button);
		}
		group.getToggles().get(0).setSelected(true);
		
		group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
			if (newToggle == null) {
				oldToggle.setSelected(true); // clicking on the selected tab will not unselect it
			}else {
				onSelect.accept(((ToggleButton) newToggle).getText());
			}
		});
	}

}
