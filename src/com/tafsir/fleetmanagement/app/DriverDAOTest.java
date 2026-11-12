package com.tafsir.fleetmanagement.app;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import com.tafsir.fleetmanagement.dao.DriverDAO;
import com.tafsir.fleetmanagement.model.Driver;
import com.tafsir.fleetmanagement.model.DriverStatus;

//>> Console check of the data side of TC-09 and TC-11. TC-10 (missing license type) is a screen rule: step 2.
//>> It adds a "Test Driver" row, so re-run sample_data.sql afterwards.
public class DriverDAOTest {

    public static void main(String[] args) {
        DriverDAO dao = new DriverDAO();
        try {
            List<Driver> start = dao.findAll();
            check("sample data: 10 drivers loaded", 10, start.size());   //>> not a TC: proves the new name columns work
            System.out.println("first in the list (sorted by last name): " + start.get(0).getShortName());

            int newId = dao.addDriver(new Driver(0, "Test", "Driver", "Class C", LocalDate.of(2026, 11, 2), DriverStatus.ACTIVE));
            check("TC-09 driver saved and listed", 11, dao.findAll().size());

            check("TC-11 status update accepted", true, dao.updateStatus(newId, DriverStatus.INACTIVE));
            DriverStatus stored = null;
            for (Driver d : dao.findAll()) {
                if (d.getDriverId() == newId) {
                    stored = d.getStatus();
                }
            }
            check("TC-11 driver is now Inactive", DriverStatus.INACTIVE, stored);
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void check(String name, Object expected, Object actual) {
        String result = expected.equals(actual) ? "PASS" : "FAIL";
        System.out.println(result + " - " + name + " (expected " + expected + ", got " + actual + ")");
    }
}