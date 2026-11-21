package com.tafsir.fleetmanagement.app;

import java.time.LocalDate;
import java.util.List;

import com.tafsir.fleetmanagement.dao.MaintenanceDAO;
import com.tafsir.fleetmanagement.model.MaintenanceRecord;

//>> Console check of the maintenance data layer (groundwork for TC-18 and TC-19).
//>> It adds one row, so re-run sample_data.sql afterwards.
public class MaintenanceDAOTest {
	public static void main(String[] args) throws Exception {
		MaintenanceDAO dao = new MaintenanceDAO();
		int allBefore = dao.findAll().size();
		int v4Before = dao.findByVehicle(4).size();

		// 1. Sample data is loaded
		System.out.println(allBefore > 0 ? "PASS sample data: " + allBefore + " records"
				: "FAIL no maintenance records found");

		// 2. Save a record
		int id = dao.addRecord(new MaintenanceRecord(0, 4, LocalDate.of(2026, 11, 19),
				"Brake inspection", 249.99, 12000));
		System.out.println(id > 0 ? "PASS addRecord returned id " + id : "FAIL addRecord");

		// 3. It reads back first (newest) with the saved values
		List<MaintenanceRecord> v4 = dao.findByVehicle(4);
		MaintenanceRecord first = v4.get(0);
		System.out.println(first.getMaintId() == id
				&& first.getServiceType().equals("Brake inspection")
				&& Math.abs(first.getCost() - 249.99) < 0.005
				&& first.getMileage() == 12000
				? "PASS read back: " + first.getServiceType() + ", $" + first.getCost() + ", " + first.getMileage() + " mi"
				: "FAIL read back " + first.getServiceType());

		// 4. Counts went up by exactly one, and only for V-004
		int all = dao.findAll().size();
		boolean onlyV4 = true;
		for (MaintenanceRecord r : v4) {
			if (r.getVehicleId() != 4) onlyV4 = false;
		}
		System.out.println(onlyV4 && v4.size() == v4Before + 1 && all == allBefore + 1
				? "PASS V-004 " + v4Before + " to " + v4.size() + ", fleet " + allBefore + " to " + all
				: "FAIL V-004 " + v4.size() + ", fleet " + all);
	}
}