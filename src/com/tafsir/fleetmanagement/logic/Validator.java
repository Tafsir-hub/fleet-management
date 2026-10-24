package com.tafsir.fleetmanagement.logic;

import com.tafsir.fleetmanagement.model.VehicleStatus;

public class Validator {
	
	// True only if every value has some text in it.
	public boolean requireFields(String...values) {
		if (values == null) {
			return false;			
		}
		for (String value : values) {
			if (value == null || value.trim().isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	// Mileage can stay the same or go up, never down,and never below 0
	public boolean validMileage(int oldMileage, int newMileage) {
		return newMileage >= 0 && newMileage>= oldMileage;
	}
	
	// True only for the supported status (i.e ACTIVE, RETIRED, IN_REPAIR)
	public boolean validStatus(VehicleStatus status) {
		return status != null;
	}
}
