package com.tafsir.fleetmanagement.ui.components;

// This is going to be the deck's colors. Every component reads from this
public class Theme {
	
	public static final String NAVY = "#1E2761";
	public static final String TEAL = "#007A82";
	public static final String ORANGE = "#D94F1E";
	public static final String ICE = "#CADCFC";
	public static final String BACKGROUND = "#F3F5FC";
	public static final String TEXT = "#20264A";
	public static final String MUTED = "#5B6690";
	
	private Theme() {
		
	}
	
	public static String stylesheet() {
		return Theme.class.getResource("/fmas.css").toExternalForm();
	}

}
