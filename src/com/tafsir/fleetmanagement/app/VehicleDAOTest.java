package com.tafsir.fleetmanagement.app;

import com.tafsir.fleetmanagement.dao.VehicleDAO;
import com.tafsir.fleetmanagement.model.Vehicle;

import java.sql.SQLException;
import java.util.List;

public class VehicleDAOTest {

	public static void main(String[] args ) {
		
		try {
			List<Vehicle> vehicles = new VehicleDAO().findAll();
			System.out.println("Vehicles found: " + vehicles.size());
			
			for (Vehicle v : vehicles) {
				System.out.println(v);
			}
		} catch (SQLException e) {
			System.out.println("Database error:" + e.getMessage());
		}
		
	}

}
