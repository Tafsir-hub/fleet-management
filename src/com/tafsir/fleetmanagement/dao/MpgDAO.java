package com.tafsir.fleetmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.model.MpgSummary;

public class MpgDAO extends DBConnection {

	// Miles come from the trips table and gallons from the fuel table. Each is added up on its own
	// (one subquery each) and shown side by side. Joining both tables directly would repeat rows
	// and inflate the totals, the same reason CostDAO works this way.
	private static final String SQL =
		"SELECT v.vehicle_id, "
		+ "(SELECT COALESCE(SUM(t.miles), 0) FROM trips t WHERE t.vehicle_id = v.vehicle_id "
		+ "   AND (? IS NULL OR t.trip_date >= ?) AND (? IS NULL OR t.trip_date <= ?)) AS miles, "
		+ "(SELECT COALESCE(SUM(f.gallons), 0) FROM fuel f WHERE f.vehicle_id = v.vehicle_id "
		+ "   AND (? IS NULL OR f.fuel_date >= ?) AND (? IS NULL OR f.fuel_date <= ?)) AS gallons "
		+ "FROM vehicles v "
		+ "WHERE (? IS NULL OR v.vehicle_id = ?) "
		+ "ORDER BY v.vehicle_id";

	// One row per vehicle. null means "no filter": all vehicles, or all dates.
	public List<MpgSummary> findMpg(Integer vehicleId, LocalDate from, LocalDate to) throws SQLException {
		java.sql.Date fromSql = from == null ? null : java.sql.Date.valueOf(from);
		java.sql.Date toSql = to == null ? null : java.sql.Date.valueOf(to);

		List<MpgSummary> list = new ArrayList<>();
		try (Connection c = getConnection();
				PreparedStatement ps = c.prepareStatement(SQL)) {
			// 1-4: the trip dates, 5-8: the fuel dates, 9-10: the vehicle
			for (int i = 0; i < 2; i++) {
				ps.setDate(1 + i * 4, fromSql);
				ps.setDate(2 + i * 4, fromSql);
				ps.setDate(3 + i * 4, toSql);
				ps.setDate(4 + i * 4, toSql);
			}
			ps.setObject(9, vehicleId);
			ps.setObject(10, vehicleId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(new MpgSummary(rs.getInt("vehicle_id"),
							rs.getDouble("miles"), rs.getDouble("gallons")));
				}
			}
		}
		return list;
	}
}