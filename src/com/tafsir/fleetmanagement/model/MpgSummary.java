package com.tafsir.fleetmanagement.model;

// One row of the fuel efficiency (MPG) report: the miles driven and the gallons bought for one vehicle.
// MPG is not stored in the database. It is worked out here: miles / gallons.
public class MpgSummary {

	private final int vehicleId;
	private final double miles;
	private final double gallons;

	public MpgSummary(int vehicleId, double miles, double gallons) {
		this.vehicleId = vehicleId;
		this.miles = miles;
		this.gallons = gallons;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public double getMiles() {
		return miles;
	}

	public double getGallons() {
		return gallons;
	}

	// Miles per gallon, rounded to one decimal.
	// null when no fuel was bought (gallons is 0), because dividing by zero has no answer.
	public Double getMpg() {
		if (gallons <= 0) return null;
		return Math.round(miles / gallons * 10.0) / 10.0;
	}
}