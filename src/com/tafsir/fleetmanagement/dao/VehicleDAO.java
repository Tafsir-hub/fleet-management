package com.tafsir.fleetmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;

public class VehicleDAO extends DBConnection {

	public List<Vehicle> findAll() throws SQLException{
		String sql = "SELECT vehicle_id, make_model, year,vehicle_type, mileage,status " 
					+ "FROM vehicles ORDER BY vehicle_id";
		List<Vehicle> vehicles = new ArrayList<>();
	try (Connection conn = getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery()){
		while (rs.next()) {
			vehicles.add(toVehicle(rs));
		}
		
	}
	return vehicles;
	}

	private Vehicle toVehicle(ResultSet rs) throws SQLException {

		return new Vehicle(
				rs.getInt("vehicle_id"),
				rs.getString("make_model"),
				rs.getInt("year"),
				rs.getString("vehicle_type"),
				rs.getInt("mileage"),
				VehicleStatus.valueOf(rs.getString("status")));
			
	}

}
