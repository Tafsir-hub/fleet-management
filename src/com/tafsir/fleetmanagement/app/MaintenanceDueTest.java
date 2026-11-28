package com.tafsir.fleetmanagement.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tafsir.fleetmanagement.dao.MaintenanceDAO;
import com.tafsir.fleetmanagement.dao.MaintenanceDueDAO;
import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.logic.MaintenanceRule;
import com.tafsir.fleetmanagement.model.DueVehicle;
import com.tafsir.fleetmanagement.model.MaintenanceRecord;
import com.tafsir.fleetmanagement.model.ServiceStatus;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;

//>> Console check of the maintenance-due rule and query (groundwork for TC-20 and TC-21).
//>> Part A tests the rule alone with made-up vehicles and a fixed date. Part B runs it on the real database.
//>> It only reads, so there is nothing to reset afterwards.
public class MaintenanceDueTest {

	static int failures = 0;

	static void check(String name, boolean ok) {
		System.out.println((ok ? "PASS " : "FAIL ") + name);
		if (!ok) failures++;
	}

	// Part A: the rule on its own. Today is fixed at 06/01/2026.
	static void ruleChecks() {
		MaintenanceRule rule = new MaintenanceRule();
		LocalDate today = LocalDate.of(2026, 6, 1);

		check("recent service, few miles: not due",
				rule.check(new ServiceStatus(1, VehicleStatus.ACTIVE, 10100, today.minusDays(10), 10000), today) == null);
		check("4,999 miles since service: not due",
				rule.check(new ServiceStatus(2, VehicleStatus.ACTIVE, 14999, today.minusDays(10), 10000), today) == null);
		DueVehicle byMiles = rule.check(new ServiceStatus(3, VehicleStatus.ACTIVE, 15000, today.minusDays(10), 10000), today);
		check("5,000 miles since service: due, reason miles", byMiles != null && byMiles.getMilesSince() == 5000
				&& byMiles.getReason().equals("Over 5,000 miles"));
		check("179 days since service: not due",
				rule.check(new ServiceStatus(4, VehicleStatus.ACTIVE, 10100, today.minusDays(179), 10000), today) == null);
		DueVehicle byDays = rule.check(new ServiceStatus(5, VehicleStatus.ACTIVE, 10100, today.minusDays(180), 10000), today);
		check("180 days since service: due, reason days", byDays != null && byDays.getDaySince() == 180
				&& byDays.getReason().equals("Over 180 days"));
		DueVehicle both = rule.check(new ServiceStatus(6, VehicleStatus.ACTIVE, 20000, today.minusDays(300), 10000), today);
		check("both limits passed: reason names both", both != null && both.getReason().equals("Over 5,000 miles and 180 days"));
		DueVehicle never = rule.check(new ServiceStatus(7, VehicleStatus.ACTIVE, 500, null, null), today);
		check("never serviced: due", never != null && never.getReason().equals("Never serviced") && never.getMilesSince() == null);
		check("retired vehicle never serviced: not listed",
				rule.check(new ServiceStatus(8, VehicleStatus.RETIRED, 500, null, null), today) == null);
		check("retired vehicle long overdue: not listed",
				rule.check(new ServiceStatus(9, VehicleStatus.RETIRED, 90000, today.minusDays(900), 10000), today) == null);
		check("in repair vehicle long overdue: due",
				rule.check(new ServiceStatus(10, VehicleStatus.IN_REPAIR, 90000, today.minusDays(900), 10000), today) != null);
		check("service odometer above current odometer: miles since is 0, not due",
				rule.check(new ServiceStatus(11, VehicleStatus.ACTIVE, 9000, today.minusDays(10), 10000), today) == null);
	}

	// Part B: the real database. The expected list is worked out in plain Java from VehicleDAO and MaintenanceDAO,
	// a different path than the query used by MaintenanceDueDAO.
	static void databaseChecks() throws Exception {
		LocalDate today = LocalDate.now();
		MaintenanceRule rule = new MaintenanceRule();

		List<ServiceStatus> statuses = new MaintenanceDueDAO().findServiceStatus();
		List<Vehicle> vehicles = new VehicleDAO().findAll(null, null);
		List<MaintenanceRecord> records = new MaintenanceDAO().findAll();

		check("one row per vehicle (" + vehicles.size() + ")", statuses.size() == vehicles.size());

		// Last service of each vehicle, in plain Java (newest date, then highest id)
		boolean lastOk = true;
		List<ServiceStatus> expectedStatuses = new ArrayList<>();
		for (Vehicle v : vehicles) {
			MaintenanceRecord last = null;
			for (MaintenanceRecord m : records) {
				if (m.getVehicleId() != v.getVehicleId()) continue;
				if (last == null || m.getServiceDate().isAfter(last.getServiceDate())
						|| (m.getServiceDate().isEqual(last.getServiceDate()) && m.getMaintId() > last.getMaintId())) {
					last = m;
				}
			}
			ServiceStatus s = new ServiceStatus(v.getVehicleId(), v.getStatus(), v.getMileage(),
					last == null ? null : last.getServiceDate(), last == null ? null : last.getMileage());
			expectedStatuses.add(s);

			for (ServiceStatus q : statuses) {
				if (q.getVehicleId() != v.getVehicleId()) continue;
				boolean sameDate = (q.getLastServiceDate() == null) ? s.getLastServiceDate() == null
						: q.getLastServiceDate().equals(s.getLastServiceDate());
				boolean sameMiles = (q.getLastServiceMileage() == null) ? s.getLastServiceMileage() == null
						: q.getLastServiceMileage().equals(s.getLastServiceMileage());
				if (!sameDate || !sameMiles || q.getMileage() != s.getMileage() || q.getStatus() != s.getStatus()) {
					lastOk = false;
					System.out.println("  differs for V-" + String.format("%03d", v.getVehicleId()));
				}
			}
		}
		check("last service date and mileage match the plain-Java lookup", lastOk);

		Set<Integer> due = new HashSet<>();
		for (DueVehicle d : rule.findDue(statuses, today)) due.add(d.getVehicleId());
		Set<Integer> expectedDue = new HashSet<>();
		for (DueVehicle d : rule.findDue(expectedStatuses, today)) expectedDue.add(d.getVehicleId());
		check("due list equals the plain-Java list (" + due.size() + " due)", due.equals(expectedDue));

		boolean noRetired = true;
		for (Vehicle v : vehicles) {
			if (v.getStatus() == VehicleStatus.RETIRED && due.contains(v.getVehicleId())) noRetired = false;
		}
		check("no retired vehicle in the due list", noRetired);

		System.out.println("INFO due today (" + today + "):");
		for (DueVehicle d : rule.findDue(statuses, today)) {
			System.out.println("  V-" + String.format("%03d", d.getVehicleId()) + "  last service "
					+ (d.getLastServiceDate() == null ? "never" : d.getLastServiceDate())
					+ "  " + d.getReason());
		}
		System.out.println("INFO not due:");
		for (ServiceStatus s : statuses) {
			if (!due.contains(s.getVehicleId())) {
				System.out.println("  V-" + String.format("%03d", s.getVehicleId()) + "  " + s.getStatus());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println("--- Part A: the rule ---");
		ruleChecks();
		System.out.println("--- Part B: the database ---");
		databaseChecks();
		System.out.println(failures == 0 ? "ALL PASSED" : failures + " FAILED");
	}
}