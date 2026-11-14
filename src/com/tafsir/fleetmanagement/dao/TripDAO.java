package com.tafsir.fleetmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.model.Trip;

public class TripDAO extends DBConnection {

    // Saves a trip and returns the new trip_id
    public int addTrip(Trip t) throws SQLException {
        String sql = "INSERT INTO trips (vehicle_id, driver_id, trip_date, origin, destination, miles) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, t.getVehicleId());
            ps.setInt(2, t.getDriverId());
            ps.setDate(3, java.sql.Date.valueOf(t.getTripDate()));
            ps.setString(4, t.getOrigin());
            ps.setString(5, t.getDestination());
            ps.setDouble(6, t.getMiles());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : -1;
            }
        }
    }

    // Trip history for one vehicle, newest first, with the driver's name from the drivers table
    public List<Trip> findByVehicle(int vehicleId) throws SQLException {
        String sql = "SELECT t.trip_id, t.vehicle_id, t.driver_id, t.trip_date, t.origin, "
                   + "t.destination, t.miles, CONCAT(LEFT(d.first_name, 1), '. ', d.last_name) AS driver_name "
                   + "FROM trips t JOIN drivers d ON d.driver_id = t.driver_id "
                   + "WHERE t.vehicle_id = ? ORDER BY t.trip_date DESC, t.trip_id DESC";
        List<Trip> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Trip t = new Trip(rs.getInt("trip_id"), rs.getInt("vehicle_id"),
                            rs.getInt("driver_id"), rs.getDate("trip_date").toLocalDate(),
                            rs.getString("origin"), rs.getString("destination"),
                            rs.getDouble("miles"));
                    t.setDriverName(rs.getString("driver_name"));
                    list.add(t);
                }
            }
        }
        return list;
    }
}