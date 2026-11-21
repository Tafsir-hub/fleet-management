package com.tafsir.fleetmanagement.model;

import java.time.LocalDate;

public class MaintenanceRecord {
	
	private int maintId;
	private int vehicleId;
	private LocalDate serviceDate;
	private String serviceType;
	private double cost;
	private int mileage;
	
	
	
	public MaintenanceRecord(int maintId, int vehicleId, LocalDate serviceDate, String serviceType, double cost,
			int mileage) {
		super();
		this.maintId = maintId;
		this.vehicleId = vehicleId;
		this.serviceDate = serviceDate;
		this.serviceType = serviceType;
		this.cost = cost;
		this.mileage = mileage;
	}
	public int getMaintId() {
		return maintId;
	}
	public void setMaintId(int maintId) {
		this.maintId = maintId;
	}
	public int getVehicleId() {
		return vehicleId;
	}
	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}
	public LocalDate getServiceDate() {
		return serviceDate;
	}
	public void setServiceDate(LocalDate serviceDate) {
		this.serviceDate = serviceDate;
	}
	public String getServiceType() {
		return serviceType;
	}
	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	public int getMileage() {
		return mileage;
	}
	public void setMileage(int mileage) {
		this.mileage = mileage;
	}

}
