package com.tafsir.fleetmanagement.ui.components;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

//This is basically the frame every screen sits in: header on top, sidebar on the left and content in the middle
public class AppShell extends BorderPane {
	
	public AppShell(String title, String selectedNav, Node content, Consumer<String> onNavigate) {
		setTop(new HeaderBar(title));
		setLeft(new Sidebar(selectedNav, onNavigate));
		setCenter(content);
		
	}

}
