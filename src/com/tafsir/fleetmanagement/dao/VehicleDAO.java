package com.tafsir.fleetmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.model.Vehicle;
import com.tafsir.fleetmanagement.model.VehicleStatus;

public class VehicleDAO extends DBConnection {

	private static final String COLUMNS = 
			"vehicle_id, make_model, year, vehicle_type, mileage, status";
	
	public List<Vehicle> findAll() throws SQLException {
	    return findAll(null, null);
	}
	
	public List<Vehicle> findAll(VehicleStatus status, String type) throws SQLException {
		StringBuilder sql = new StringBuilder ("SELECT " + COLUMNS + " FROM vehicles");
		List<String> values = new ArrayList<>();
		String joiner = " WHERE ";
		if (status != null) {
			sql.append(joiner).append("status = ?");
			values.add(status.name());
			joiner = " AND ";
		}
		if (type != null && !type.trim().isEmpty()) {
			sql.append(joiner).append("vehicle_type = ?");
			values.add(type.trim());
		}
		sql.append(" ORDER BY vehicle_id");
		
		List<Vehicle> vehicles = new ArrayList<>();
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql.toString())){
			for (int i=0; i< values.size(); i++) {
				ps.setString(i+1, values.get(i));
			}
			try (ResultSet rs = ps.executeQuery()){
				while (rs.next()) {
					vehicles.add(toVehicle(rs));
				}
			}
		}
		return vehicles;
	}
	
	public int addVehicle(Vehicle v) throws SQLException {
		String sql = "INSERT INTO vehicles (make_model, year, vehicle_type, mileage, status) "
				+ "VALUES (?, ?, ?, ?, ?)";
		VehicleStatus status = (v.getStatus() == null) ? VehicleStatus.ACTIVE : v.getStatus();
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, v.getMakeModel());
			ps.setInt(2, v.getYear());
			ps.setString(3, v.getVehicleType());
			ps.setInt(4, v.getMileage());
			ps.setString(5, status.name());
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next()) {
					return keys.getInt(1);
				}
			}
			
		}
		throw new SQLException("The vehicle was added but no ID was returned.");
		
	}
	
	public boolean updateMileage(int vehicleId, int mileage) throws SQLException {
		String sql = "UPDATE vehicles SET mileage = ? WHERE vehicle_id = ?";
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, mileage);
			ps.setInt(2, vehicleId);
			return ps.executeUpdate() == 1;
		}
		
	}
	
	public boolean updateStatus(int vehicleId, VehicleStatus status) throws SQLException {
		String sql = "UPDATE vehicles SET status = ? WHERE vehicle_id = ?";
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, status.name());
			ps.setInt(2, vehicleId);
			return ps.executeUpdate()== 1;
		}
	
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
