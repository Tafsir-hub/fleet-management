package com.tafsir.fleetmanagement.app;

import java.time.LocalDate;
import java.util.List;

import com.tafsir.fleetmanagement.dao.DriverDAO;
import com.tafsir.fleetmanagement.dao.TripDAO;
import com.tafsir.fleetmanagement.model.Driver;
import com.tafsir.fleetmanagement.model.Trip;


public class TripDAOTest {
    public static void main(String[] args) throws Exception {
        TripDAO tripDAO = new TripDAO();
        DriverDAO driverDAO = new DriverDAO();

        // 1. Add a trip with decimal miles
        int id = tripDAO.addTrip(new Trip(0, 1, 1, LocalDate.of(2026, 11, 14),
                "Newark, NJ", "Philadelphia, PA", 94.5));
        System.out.println(id > 0 ? "PASS addTrip returned id " + id : "FAIL addTrip");

        // 2. The new trip is in V-001's history, newest first, with decimal miles kept
        List<Trip> history = tripDAO.findByVehicle(1);
        Trip first = history.get(0);
        System.out.println(first.getTripId() == id && first.getMiles() == 94.5
                ? "PASS newest trip first, miles 94.5" : "FAIL newest trip / miles");

        // 3. Dates never go up as you move down the list
        boolean sorted = true;
        for (int i = 1; i < history.size(); i++) {
            if (history.get(i).getTripDate().isAfter(history.get(i - 1).getTripDate())) sorted = false;
        }
        System.out.println(sorted ? "PASS history sorted, " + history.size() + " trips"
                                  : "FAIL history order");

        // 4. The join gives a driver name like "J. Alvarez"
        System.out.println("J. Alvarez".equals(first.getDriverName())
                ? "PASS driver name from join: " + first.getDriverName()
                : "FAIL driver name: " + first.getDriverName());

        // 5. findActive returns only Active drivers
        List<Driver> active = driverDAO.findActive();
        boolean allActive = active.stream().allMatch(d -> d.getStatus().getLabel().equals("Active"));
        System.out.println(allActive ? "PASS findActive: " + active.size() + " active drivers"
                                     : "FAIL findActive");
    }
}