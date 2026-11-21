package com.tafsir.fleetmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.model.MaintenanceRecord;

public class MaintenanceDAO extends DBConnection {

	private static final String SELECT =
		"SELECT maint_id, vehicle_id, service_date, service_type, cost, mileage FROM maintenance ";

	private static final String ORDER = "ORDER BY service_date DESC, maint_id DESC";

	// Saves a service record and returns the new maint_id
	public int addRecord(MaintenanceRecord r) throws SQLException {
		String sql = "INSERT INTO maintenance (vehicle_id, service_date, service_type, cost, mileage) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try (Connection c = getConnection();
				PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			ps.setInt(1, r.getVehicleId());
			ps.setDate(2, java.sql.Date.valueOf(r.getServiceDate()));
			ps.setString(3, r.getServiceType());
			ps.setDouble(4, r.getCost());
			ps.setInt(5, r.getMileage());
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				return keys.next() ? keys.getInt(1) : -1;
			}
		}
	}

	// One vehicle's maintenance history, newest first
	public List<MaintenanceRecord> findByVehicle(int vehicleId) throws SQLException {
		List<MaintenanceRecord> list = new ArrayList<>();
		try (Connection c = getConnection();
				PreparedStatement ps = c.prepareStatement(SELECT + "WHERE vehicle_id = ? " + ORDER)) {
			ps.setInt(1, vehicleId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) list.add(toRecord(rs));
			}
		}
		return list;
	}

	// Every vehicle's records, newest first
	public List<MaintenanceRecord> findAll() throws SQLException {
		List<MaintenanceRecord> list = new ArrayList<>();
		try (Connection c = getConnection();
				PreparedStatement ps = c.prepareStatement(SELECT + ORDER);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) list.add(toRecord(rs));
		}
		return list;
	}

	private MaintenanceRecord toRecord(ResultSet rs) throws SQLException {
		return new MaintenanceRecord(rs.getInt("maint_id"), rs.getInt("vehicle_id"),
				rs.getDate("service_date").toLocalDate(), rs.getString("service_type"),
				rs.getDouble("cost"), rs.getInt("mileage"));
	}

}