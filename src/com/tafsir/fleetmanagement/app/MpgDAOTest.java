package com.tafsir.fleetmanagement.app;

import java.time.LocalDate;
import java.util.List;

import com.tafsir.fleetmanagement.dao.FuelDAO;
import com.tafsir.fleetmanagement.dao.MpgDAO;
import com.tafsir.fleetmanagement.dao.TripDAO;
import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.model.FuelEntry;
import com.tafsir.fleetmanagement.model.MpgSummary;
import com.tafsir.fleetmanagement.model.Trip;

//>> Console check of the fuel efficiency (MPG) query (groundwork for TC-17).
//>> It only reads, so there is nothing to reset afterwards.
public class MpgDAOTest {

	// Adds up the miles for one vehicle in plain Java (null dates = all dates)
	static double expectedMiles(int vehicleId, LocalDate from, LocalDate to) throws Exception {
		double sum = 0;
		for (Trip t : new TripDAO().find(null, null)) {
			if (t.getVehicleId() == vehicleId && inRange(t.getTripDate(), from, to)) sum += t.getMiles();
		}
		return sum;
	}

	// Adds up the gallons for one vehicle in plain Java
	static double expectedGallons(int vehicleId, LocalDate from, LocalDate to) throws Exception {
		double sum = 0;
		for (FuelEntry f : new FuelDAO().findAll()) {
			if (f.getVehicleId() == vehicleId && inRange(f.getFuelDate(), from, to)) sum += f.getGallons();
		}
		return sum;
	}

	static boolean inRange(LocalDate d, LocalDate from, LocalDate to) {
		return (from == null || !d.isBefore(from)) && (to == null || !d.isAfter(to));
	}

	public static void main(String[] args) throws Exception {
		MpgDAO dao = new MpgDAO();
		int vehicleCount = new VehicleDAO().findAll(null, null).size();

		// 1. One row per vehicle, all dates
		List<MpgSummary> all = dao.findMpg(null, null, null);
		System.out.println(all.size() == vehicleCount ? "PASS one row per vehicle: " + all.size()
				: "FAIL expected " + vehicleCount + " rows, got " + all.size());

		// 2. Each vehicle's miles and gallons match the plain-Java totals
		boolean match = true;
		double fleetMiles = 0;
		double fleetGallons = 0;
		for (MpgSummary s : all) {
			double m = expectedMiles(s.getVehicleId(), null, null);
			double g = expectedGallons(s.getVehicleId(), null, null);
			fleetMiles += s.getMiles();
			fleetGallons += s.getGallons();
			if (Math.abs(s.getMiles() - m) > 0.05 || Math.abs(s.getGallons() - g) > 0.05) {
				match = false;
				System.out.println("  mismatch V-" + s.getVehicleId() + ": query " + s.getMiles() + " mi / "
						+ s.getGallons() + " gal, expected " + m + " / " + g);
			}
		}
		System.out.println(match ? "PASS miles and gallons match for every vehicle" : "FAIL some totals do not match");

		// 3. MPG is miles divided by gallons, rounded to one decimal
		boolean mpgOk = true;
		for (MpgSummary s : all) {
			if (s.getGallons() > 0) {
				double e = Math.round(s.getMiles() / s.getGallons() * 10.0) / 10.0;
				if (s.getMpg() == null || Math.abs(s.getMpg() - e) > 0.001) mpgOk = false;
			}
		}
		System.out.println(mpgOk ? "PASS MPG = miles / gallons for every vehicle with fuel" : "FAIL MPG value is wrong");

		// 4. A vehicle with no fuel in the range has no MPG (null), not a crash or a zero
		boolean nullOk = true;
		int checked = 0;
		for (MpgSummary s : dao.findMpg(null, LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 31))) {
			checked++;
			if (s.getMpg() != null) nullOk = false;
		}
		System.out.println(nullOk && checked == vehicleCount
				? "PASS no fuel in the range gives no MPG for all " + checked + " vehicles"
				: "FAIL empty range should give null MPG");

		// 5. One vehicle only (V-004)
		List<MpgSummary> v4 = dao.findMpg(4, null, null);
		System.out.println(v4.size() == 1 && v4.get(0).getVehicleId() == 4
				? String.format("PASS V-004 only: %,.1f mi / %,.1f gal = %s MPG",
						v4.get(0).getMiles(), v4.get(0).getGallons(), v4.get(0).getMpg())
				: "FAIL V-004 filter returned " + v4.size() + " rows");

		// 6. Date range (both ends included)
		LocalDate from = LocalDate.of(2026, 9, 1);
		LocalDate to = LocalDate.of(2026, 9, 30);
		boolean rangeOk = true;
		for (MpgSummary s : dao.findMpg(null, from, to)) {
			if (Math.abs(s.getMiles() - expectedMiles(s.getVehicleId(), from, to)) > 0.05
					|| Math.abs(s.getGallons() - expectedGallons(s.getVehicleId(), from, to)) > 0.05) rangeOk = false;
		}
		System.out.println(rangeOk ? "PASS September 2026 matches the plain-Java totals" : "FAIL date range totals differ");

		// 7. Fleet MPG (total miles / total gallons), the number the dashboard KPI shows
		if (fleetGallons > 0) {
			System.out.println(String.format("INFO fleet: %,.1f miles / %,.1f gallons = %.1f MPG",
					fleetMiles, fleetGallons, fleetMiles / fleetGallons));
		}
	}
}