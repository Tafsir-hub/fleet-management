package com.tafsir.fleetmanagement.ui.components;

import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;

//This is basically the frame every screen sits in: header on top, sidebar on the left and content in the middle
public class AppShell extends BorderPane {

	private final Sidebar sidebar;

	public AppShell(String title, String selectedNav, Node content, Consumer<String> onNavigate) {
		sidebar = new Sidebar(selectedNav, onNavigate);
		setTop(new HeaderBar(title));
		setLeft(sidebar);
		setCenter(content);
	}

	// Moves the sidebar highlight without opening a screen
	public void selectNav(String name) {
		sidebar.select(name);
	}
}
		
	
