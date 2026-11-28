package com.tafsir.fleetmanagement.model;

import java.time.LocalDate;

public class DueVehicle {
	
	private final int vehicleId;
	private final LocalDate lastServiceDate;
	private final Integer milesSince;
	private final Long daySince;
	private final String reason;
	public DueVehicle(int vehicleId, LocalDate lastServiceDate, Integer milesSince, Long daySince, String reason) {
		super();
		this.vehicleId = vehicleId;
		this.lastServiceDate = lastServiceDate;
		this.milesSince = milesSince;
		this.daySince = daySince;
		this.reason = reason;
	}
	public int getVehicleId() {
		return vehicleId;
	}
	public LocalDate getLastServiceDate() {
		return lastServiceDate;
	}
	public Integer getMilesSince() {
		return milesSince;
	}
	public Long getDaySince() {
		return daySince;
	}
	public String getReason() {
		return reason;
	}
}
