package com.tafsir.fleetmanagement.app;

import java.time.LocalDate;
import java.util.List;

import com.tafsir.fleetmanagement.dao.CostDAO;
import com.tafsir.fleetmanagement.dao.FuelDAO;
import com.tafsir.fleetmanagement.dao.MaintenanceDAO;
import com.tafsir.fleetmanagement.model.CostSummary;
import com.tafsir.fleetmanagement.model.FuelEntry;
import com.tafsir.fleetmanagement.model.MaintenanceRecord;

//>> Console check of the operating cost query (groundwork for TC-22).
//>> It only reads, so there is nothing to reset afterwards.
public class CostDAOTest {

	// Adds up fuel and maintenance for one vehicle in plain Java (null dates = all dates)
	static double expected(int vehicleId, LocalDate from, LocalDate to) throws Exception {
		double sum = 0;
		for (FuelEntry f : new FuelDAO().findAll()) {
			if (f.getVehicleId() == vehicleId && inRange(f.getFuelDate(), from, to)) sum += f.getTotalCost();
		}
		for (MaintenanceRecord m : new MaintenanceDAO().findAll()) {
			if (m.getVehicleId() == vehicleId && inRange(m.getServiceDate(), from, to)) sum += m.getCost();
		}
		return sum;
	}

	static boolean inRange(LocalDate d, LocalDate from, LocalDate to) {
		return (from == null || !d.isBefore(from)) && (to == null || !d.isAfter(to));
	}

	public static void main(String[] args) throws Exception {
		CostDAO dao = new CostDAO();

		// 1. One row per vehicle, all dates
		List<CostSummary> all = dao.findCosts(null, null, null);
		System.out.println(all.size() == 10 ? "PASS one row per vehicle: " + all.size()
				: "FAIL expected 10 rows, got " + all.size());

		// 2. Each vehicle's total matches the plain-Java total (fuel + maintenance)
		boolean match = true;
		double fleet = 0;
		for (CostSummary s : all) {
			double e = expected(s.getVehicleId(), null, null);
			fleet += s.getTotal();
			if (Math.abs(s.getTotal() - e) > 0.01) {
				match = false;
				System.out.println("  mismatch V-" + s.getVehicleId() + ": query " + s.getTotal() + ", expected " + e);
			}
		}
		System.out.println(match ? String.format("PASS all totals match, fleet total $%,.2f", fleet)
				: "FAIL some totals do not match");

		// 3. One vehicle only
		List<CostSummary> v4 = dao.findCosts(4, null, null);
		System.out.println(v4.size() == 1 && v4.get(0).getVehicleId() == 4
				? String.format("PASS V-004 only: fuel $%,.2f + maintenance $%,.2f = $%,.2f",
						v4.get(0).getFuelCost(), v4.get(0).getMaintenanceCost(), v4.get(0).getTotal())
				: "FAIL V-004 filter returned " + v4.size() + " rows");

		// 4. Date range (both ends included)
		LocalDate from = LocalDate.of(2026, 9, 1);
		LocalDate to = LocalDate.of(2026, 9, 30);
		boolean rangeOk = true;
		double rangeTotal = 0;
		for (CostSummary s : dao.findCosts(null, from, to)) {
			rangeTotal += s.getTotal();
			if (Math.abs(s.getTotal() - expected(s.getVehicleId(), from, to)) > 0.01) rangeOk = false;
		}
		System.out.println(rangeOk ? String.format("PASS Sep 2026 totals match, fleet $%,.2f", rangeTotal)
				: "FAIL date range totals do not match");

		// 5. A range with no records gives zero, not an error
		double none = 0;
		for (CostSummary s : dao.findCosts(null, LocalDate.of(2000, 1, 1), LocalDate.of(2000, 1, 31))) {
			none += s.getTotal();
		}
		System.out.println(none == 0 ? "PASS empty range gives $0.00" : "FAIL empty range gave " + none);
	}
}