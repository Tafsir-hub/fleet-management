package com.tafsir.fleetmanagement.model;

import java.time.LocalDate;

public class Trip {
	
	private int tripId;
	private int vehicleId;
	private int driverId;
	private LocalDate tripDate;
	private String origin;
	private String destination;
	private double miles;
	private String driverName;
	
	
	public Trip(int tripId, int vehicleId, int driverId, LocalDate tripDate, String origin, String destination,
			double miles) {
		super();
		this.tripId = tripId;
		this.vehicleId = vehicleId;
		this.driverId = driverId;
		this.tripDate = tripDate;
		this.origin = origin;
		this.destination = destination;
		this.miles = miles;

	}
	public int getTripId() {
		return tripId;
	}
	public int getVehicleId() {
		return vehicleId;
	}
	public int getDriverId() {
		return driverId;
	}
	public LocalDate getTripDate() {
		return tripDate;
	}
	public String getOrigin() {
		return origin;
	}
	public String getDestination() {
		return destination;
	}
	public double getMiles() {
		return miles;
	}
	public String getDriverName() {
		return driverName;
	}
	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}
	
	
	
	

}
