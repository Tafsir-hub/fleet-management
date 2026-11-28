package com.tafsir.fleetmanagement.model;

import java.time.LocalDate;

public class ServiceStatus {
	
	//>> The facts needed to decide if a vehicle is due for service: its status, its odometer now,
	//>> and when (and at what odometer reading) it was last serviced. No service yet = null, null.
	
	private final int vehicleId;
	private final VehicleStatus status;
	private final int mileage;
	private final LocalDate lastServiceDate;
	private final Integer lastServiceMileage;
	
	public ServiceStatus(int vehicleId, VehicleStatus status, int mileage, LocalDate lastServiceDate,
			Integer lastServiceMileage) {
		super();
		this.vehicleId = vehicleId;
		this.status = status;
		this.mileage = mileage;
		this.lastServiceDate = lastServiceDate;
		this.lastServiceMileage = lastServiceMileage;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public VehicleStatus getStatus() {
		return status;
	}

	public int getMileage() {
		return mileage;
	}

	public LocalDate getLastServiceDate() {
		return lastServiceDate;
	}

	public Integer getLastServiceMileage() {
		return lastServiceMileage;
	}	

}
