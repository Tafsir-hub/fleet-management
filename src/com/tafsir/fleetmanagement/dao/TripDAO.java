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
	
	private static final String SELECT =
            "SELECT t.trip_id, t.vehicle_id, t.driver_id, t.trip_date, t.origin, t.destination, t.miles, "
          + "CONCAT(LEFT(d.first_name, 1), '. ', d.last_name) AS driver_name "
          + "FROM trips t JOIN drivers d ON d.driver_id = t.driver_id ";
    private static final String ORDER = "ORDER BY t.trip_date DESC, t.trip_id DESC";

    // Saves a trip and returns the new trip_id
    public int addTrip(Trip t) throws SQLException {
        String insert = "INSERT INTO trips (vehicle_id, driver_id, trip_date, origin, destination, miles) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        String addMiles = "UPDATE vehicles SET mileage = mileage + ? WHERE vehicle_id = ?";
        try (Connection c = getConnection()){
        	c.setAutoCommit(false);
        	try (PreparedStatement ps = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS);
        		 PreparedStatement up = c.prepareStatement(addMiles)) {		
            ps.setInt(1, t.getVehicleId());
            ps.setInt(2, t.getDriverId());
            ps.setDate(3, java.sql.Date.valueOf(t.getTripDate()));
            ps.setString(4, t.getOrigin());
            ps.setString(5, t.getDestination());
            ps.setDouble(6, t.getMiles());
            ps.executeUpdate();
            
            int id;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                id = keys.next() ? keys.getInt(1) : -1;
           }
            
            // Vehicle mileage is a whole number, so the trip miles are rounded (94.5 adds 95)
            up.setInt(1, (int) Math.round(t.getMiles()));
            up.setInt(2, t.getVehicleId());
            up.executeUpdate();

            c.commit();
            return id;
        } catch (SQLException e) {
            c.rollback();
            throw e;
        }
    }
}
            
   
    // Any combination of filters; null means "all"
    public List<Trip> find(Integer vehicleId, Integer driverId) throws SQLException {
        String sql = SELECT
                + "WHERE (? IS NULL OR t.vehicle_id = ?) AND (? IS NULL OR t.driver_id = ?) "
                + ORDER;
        List<Trip> list = new ArrayList<>();
        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setObject(1, vehicleId);
            ps.setObject(2, vehicleId);
            ps.setObject(3, driverId);
            ps.setObject(4, driverId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(toTrip(rs));
            }
        }
        return list;
    }

    public List<Trip> findByVehicle(int vehicleId) throws SQLException {
        return find(vehicleId, null);
    }

    private Trip toTrip(ResultSet rs) throws SQLException {
        Trip t = new Trip(rs.getInt("trip_id"), rs.getInt("vehicle_id"), rs.getInt("driver_id"),
                rs.getDate("trip_date").toLocalDate(), rs.getString("origin"),
                rs.getString("destination"), rs.getDouble("miles"));
        t.setDriverName(rs.getString("driver_name"));
        return t;
    }
}