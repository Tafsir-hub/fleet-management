package com.tafsir.fleetmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.tafsir.fleetmanagement.model.CostSummary;

public class CostDAO extends DBConnection {

	// Fuel and maintenance are added up separately (one subquery each) and then shown side by side.
	// Joining both tables directly would repeat rows and inflate the totals.
	private static final String SQL =
		"SELECT v.vehicle_id, "
		+ "(SELECT COALESCE(SUM(f.total_cost), 0) FROM fuel f WHERE f.vehicle_id = v.vehicle_id "
		+ "   AND (? IS NULL OR f.fuel_date >= ?) AND (? IS NULL OR f.fuel_date <= ?)) AS fuel_cost, "
		+ "(SELECT COALESCE(SUM(m.cost), 0) FROM maintenance m WHERE m.vehicle_id = v.vehicle_id "
		+ "   AND (? IS NULL OR m.service_date >= ?) AND (? IS NULL OR m.service_date <= ?)) AS maint_cost "
		+ "FROM vehicles v "
		+ "WHERE (? IS NULL OR v.vehicle_id = ?) "
		+ "ORDER BY v.vehicle_id";

	// One row per vehicle. null means "no filter": all vehicles, or all dates.
	public List<CostSummary> findCosts(Integer vehicleId, LocalDate from, LocalDate to) throws SQLException {
		java.sql.Date fromSql = from == null ? null : java.sql.Date.valueOf(from);
		java.sql.Date toSql = to == null ? null : java.sql.Date.valueOf(to);

		List<CostSummary> list = new ArrayList<>();
		try (Connection c = getConnection();
				PreparedStatement ps = c.prepareStatement(SQL)) {
			ps.setDate(1, fromSql);
			ps.setDate(2, fromSql);
			ps.setDate(3, toSql);
			ps.setDate(4, toSql);
			ps.setDate(5, fromSql);
			ps.setDate(6, fromSql);
			ps.setDate(7, toSql);
			ps.setDate(8, toSql);
			ps.setObject(9, vehicleId);
			ps.setObject(10, vehicleId);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					list.add(new CostSummary(rs.getInt("vehicle_id"),
							rs.getDouble("fuel_cost"), rs.getDouble("maint_cost")));
				}
			}
		}
		return list;
	}
}