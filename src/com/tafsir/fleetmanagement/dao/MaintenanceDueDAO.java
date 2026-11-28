package com.tafsir.fleetmanagement.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.model.ServiceStatus;
import com.tafsir.fleetmanagement.model.VehicleStatus;

//>> Reads the facts for the maintenance-due rule: one row per vehicle with its last service.
//>> The DAO only reads; MaintenanceRule (logic) decides who is due.
public class MaintenanceDueDAO extends DBConnection {

	// The last service is the newest by date; two services on the same day: the higher id wins.
	// Both subqueries use the same ordering so date and mileage always come from the same record.
	private static final String SQL =
		"SELECT v.vehicle_id, v.status, v.mileage, "
		+ "(SELECT m.service_date FROM maintenance m WHERE m.vehicle_id = v.vehicle_id "
		+ "   ORDER BY m.service_date DESC, m.maint_id DESC LIMIT 1) AS last_date, "
		+ "(SELECT m.mileage FROM maintenance m WHERE m.vehicle_id = v.vehicle_id "
		+ "   ORDER BY m.service_date DESC, m.maint_id DESC LIMIT 1) AS last_mileage "
		+ "FROM vehicles v ORDER BY v.vehicle_id";

	public List<ServiceStatus> findServiceStatus() throws SQLException {
		List<ServiceStatus> list = new ArrayList<>();
		try (Connection c = getConnection();
				PreparedStatement ps = c.prepareStatement(SQL);
				ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				Date d = rs.getDate("last_date");
				LocalDate lastDate = d == null ? null : d.toLocalDate();
				int lastMileage = rs.getInt("last_mileage");
				Integer lastMileageOrNull = rs.wasNull() ? null : lastMileage;
				list.add(new ServiceStatus(rs.getInt("vehicle_id"),
						VehicleStatus.valueOf(rs.getString("status")),
						rs.getInt("mileage"), lastDate, lastMileageOrNull));
			}
		}
		return list;
	}
}