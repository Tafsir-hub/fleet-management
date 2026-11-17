package com.tafsir.fleetmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.model.FuelEntry;

public class FuelDAO extends DBConnection {
	
	private static final String SELECT = 
		"SELECT fuel_id, vehicle_id, fuel_date, gallons, price_per_gallon, total_cost FROM fuel ";
	
	private static final String ORDER = "ORDER BY fuel_date DESC, fuel_id DESC";
		
	// Saves a fuel purchase and returns the new fuel_id
	public int addFuel(FuelEntry f) throws SQLException {
		String sql = "INSERT INTO fuel (vehicle_id, fuel_date, gallons, price_per_gallon, total_cost) "
				+ "VALUES (?, ?, ?, ?, ?)";
		try (Connection c = getConnection();
				PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				ps.setInt(1, f.getVehicleId());
				ps.setDate(2, java.sql.Date.valueOf(f.getFuelDate()));
				ps.setDouble(3, f.getGallons());
				ps.setDouble(4, f.getPricePerGallon());
				ps.setDouble(5, f.getTotalCost());
				ps.executeUpdate();
				try(ResultSet keys = ps.getGeneratedKeys()) {
					return keys.next() ? keys.getInt(1) : -1;
					
				}
		}
		
	}
	
	// One vehicle's fuel purchases, newest first
	public List<FuelEntry> findByVehicle(int vehicleId) throws SQLException {
	    List<FuelEntry> list = new ArrayList<>();
	    try (Connection c = getConnection();
	         PreparedStatement ps = c.prepareStatement(SELECT + "WHERE vehicle_id = ? " + ORDER)) {
	        ps.setInt(1, vehicleId);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) list.add(toFuel(rs));
	        }
	    }
	    return list;
	}
	
	public List<FuelEntry> findAll() throws SQLException {
        List<FuelEntry> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT + ORDER);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(toFuel(rs));
        }
        return list;
    }
	
	private FuelEntry toFuel(ResultSet rs) throws SQLException {
		return new FuelEntry(rs.getInt("fuel_id"), rs.getInt("vehicle_id"),
				rs.getDate("fuel_date").toLocalDate(), rs.getDouble("gallons"),
				rs.getDouble("price_per_gallon"), rs.getDouble("total_cost"));
				
				
	}

}
