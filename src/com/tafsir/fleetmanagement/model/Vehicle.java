package com.tafsir.fleetmanagement.model;

public class Vehicle {
	private int vehicleId;
	private String makeModel;
	private int year;
	private String vehicleType;
	private int mileage;
	private VehicleStatus status;
	
	public Vehicle() {
		
	}

	public Vehicle(int vehicleId, String makeModel, int year, String vehicleType, int mileage, VehicleStatus status) {
		this.vehicleId = vehicleId;
		this.makeModel = makeModel;
		this.year = year;
		this.vehicleType = vehicleType;
		this.mileage = mileage;
		this.status = status;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public String getMakeModel() {
		return makeModel;
	}

	public void setMakeModel(String makeModel) {
		this.makeModel = makeModel;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public int getMileage() {
		return mileage;
	}

	public void setMileage(int mileage) {
		this.mileage = mileage;
	}

	public VehicleStatus getStatus() {
		return status;
	}

	public void setStatus(VehicleStatus status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return vehicleId + " | " + makeModel + " | " + year + " | " + vehicleType + " | " + mileage + " | " + status;
	}
	
	
	
	

}
