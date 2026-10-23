package com.tafsir.fleetmanagement.app;

import java.sql.SQLException;

import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;

public class VehicleDAOSliceTest {


	public static void main(String[] args) {
		
		VehicleDAO dao = new VehicleDAO();
		try {
			check("All vehicles", 10, dao.findAll().size());
			check("Active vehicles", 7, dao.findAll(VehicleStatus.ACTIVE, null).size());
			check("Vans", 3, dao.findAll(null, "Van").size());
			check("In Repair pickups", 1, dao.findAll(VehicleStatus.IN_REPAIR, "Pickup").size());
			
			Vehicle test = new Vehicle(0, "Test Vehicle", 2024, "Van", 0, VehicleStatus.ACTIVE);
			int newId = dao.addVehicle(test);
			System.out.println("Added vehicle with ID " + newId);
			check("Vehicles after add", 11, dao.findAll().size());
			
			check("Update mileage", true, dao.updateMileage(newId, 150));
			check("Update status", true, dao.updateStatus(newId, VehicleStatus.RETIRED));
			check("Retired vehicles", 2, dao.findAll(VehicleStatus.RETIRED, null).size());
			check("Update unknow vehicle", false, dao.updateMileage(99999, 1));
		}catch (SQLException e) {
			System.out.println("Database error: " + e.getMessage());
		}

	}

	private static void check(String name, Object expected, Object actual) {
		String result = expected.equals(actual) ? "PASS" : "FAIL";
		System.out.println(result + " - " + name + "(expected " + expected + ", got " + actual + ")");
		
	}

}
