package com.tafsir.fleetmanagement.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.model.Driver;
import com.tafsir.fleetmanagement.model.DriverStatus;

public class DriverDAO extends DBConnection {

	private static final String COLUMNS = 
			"driver_id, first_name, last_name, license_type, hire_date, status";
	
	
	
	public List<Driver> findAll() throws SQLException {
		String sql = new String ("SELECT " + COLUMNS + " FROM drivers ORDER BY last_name, first_name");
		List<Driver> drivers = new ArrayList<>();
		
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				drivers.add(toDriver(rs));
			}
		}
		return drivers;
	}
	
	public int addDriver(Driver d) throws SQLException {
		String sql = "INSERT INTO drivers (first_name, last_name, license_type, hire_date, status) "
				+ "VALUES (?, ?, ?, ?, ?)";
		DriverStatus status = (d.getStatus() == null) ? DriverStatus.ACTIVE : d.getStatus();
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, d.getFirstName());
			ps.setString(2, d.getLastName());
			ps.setString(3, d.getLicenseType());
			ps.setDate(4, Date.valueOf(d.getHireDate()));
			ps.setString(5, status.name());
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (keys.next()) {
					return keys.getInt(1);
				}
			}
			
		}
		throw new SQLException("The Driver was added but no ID was returned.");
		
	}
	
	
	public boolean updateStatus(int DriverId, DriverStatus status) throws SQLException {
		String sql = "UPDATE drivers SET status = ? WHERE driver_id = ?";
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, status.name());
			ps.setInt(2, DriverId);
			return ps.executeUpdate()== 1;
		}
	
	}

	private Driver toDriver(ResultSet rs) throws SQLException {

		return new Driver(
				rs.getInt("driver_id"),
				rs.getString("first_name"),
				rs.getString("last_name"),
				rs.getString("license_type"),
				rs.getDate("hire_date").toLocalDate(),
				DriverStatus.valueOf(rs.getString("status")));
			
	}

}
