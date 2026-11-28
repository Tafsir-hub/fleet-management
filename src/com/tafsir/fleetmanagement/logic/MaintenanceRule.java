package com.tafsir.fleetmanagement.logic;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.model.DueVehicle;
import com.tafsir.fleetmanagement.model.ServiceStatus;
import com.tafsir.fleetmanagement.model.VehicleStatus;

//>> The maintenance-due rule (UC-12), kept in one place so the numbers are easy to change.
//>> A vehicle is due when it has gone MILES_LIMIT miles or more, or DAYS_LIMIT days or more, since its last service.
//>> A vehicle that was never serviced is due. Retired vehicles are never listed.
public class MaintenanceRule {

	public static final int MILES_LIMIT = 5000;
	public static final int DAYS_LIMIT = 180;

	// The due vehicles, in vehicle order. "today" is a parameter so tests can use a fixed date.
	public List<DueVehicle> findDue(List<ServiceStatus> all, LocalDate today) {
		List<DueVehicle> due = new ArrayList<>();
		for (ServiceStatus s : all) {
			DueVehicle d = check(s, today);
			if (d != null) due.add(d);
		}
		return due;
	}

	// Null means the vehicle is not due (or is retired)
	public DueVehicle check(ServiceStatus s, LocalDate today) {
		if (s.getStatus() == VehicleStatus.RETIRED) return null;

		if (s.getLastServiceDate() == null || s.getLastServiceMileage() == null) {
			return new DueVehicle(s.getVehicleId(), null, null, null, "Never serviced");
		}

		// Never below 0, even if a service was logged with a higher reading than the current odometer
		int milesSince = Math.max(0, s.getMileage() - s.getLastServiceMileage());
		long daysSince = ChronoUnit.DAYS.between(s.getLastServiceDate(), today);

		boolean byMiles = milesSince >= MILES_LIMIT;
		boolean byDays = daysSince >= DAYS_LIMIT;
		if (!byMiles && !byDays) return null;

		String reason;
		if (byMiles && byDays) reason = "Over " + String.format("%,d", MILES_LIMIT) + " miles and " + DAYS_LIMIT + " days";
		else if (byMiles) reason = "Over " + String.format("%,d", MILES_LIMIT) + " miles";
		else reason = "Over " + DAYS_LIMIT + " days";

		return new DueVehicle(s.getVehicleId(), s.getLastServiceDate(), milesSince, daysSince, reason);
	}
}