package com.tafsir.fleetmanagement.model;

public class CostSummary {
	
	private final int vehicleId;
	private final double fuelCost;
	private final double maintenanceCost;
	public CostSummary(int vehicleId, double fuelCost, double maintenanceCost) {
		super();
		this.vehicleId = vehicleId;
		this.fuelCost = fuelCost;
		this.maintenanceCost = maintenanceCost;
	}
	public int getVehicleId() {
		return vehicleId;
	}
	public double getFuelCost() {
		return fuelCost;
	}
	public double getMaintenanceCost() {
		return maintenanceCost;
	}
	
	public double getTotal() {
		return Math.round((fuelCost + maintenanceCost) * 100.0) /100.0;}

}
