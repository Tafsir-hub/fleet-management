package com.tafsir.fleetmanagement.model;

public enum VehicleStatus {
	ACTIVE("Active"),
	IN_REPAIR("In Repair"),
	RETIRED("Retired");
	
	private final String label;
	
	VehicleStatus(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return label;
	}
	
	@Override
	public String toString() {
		return label;
	}
}
