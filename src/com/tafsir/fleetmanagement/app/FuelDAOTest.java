package com.tafsir.fleetmanagement.app;

import java.time.LocalDate;
import java.util.List;

import com.tafsir.fleetmanagement.dao.FuelDAO;
import com.tafsir.fleetmanagement.model.FuelEntry;

public class FuelDAOTest {
    public static void main(String[] args) throws Exception {
        FuelDAO dao = new FuelDAO();
        int allBefore = dao.findAll().size();
        int v4Before = dao.findByVehicle(4).size();

        // 1. Total = gallons x price (the slide 31 example)
        double total = FuelEntry.calculateTotal(18.4, 3.62);
        System.out.println(total == 66.61 ? "PASS 18.4 x 3.62 = " + total
                                          : "FAIL total was " + total);

        // 2. Rounding is half-up, the same rule as the sample data
        double rounded = FuelEntry.calculateTotal(14.45, 3.705);
        System.out.println(rounded == 53.54 ? "PASS 14.45 x 3.705 = " + rounded
                                            : "FAIL rounding gave " + rounded);

        // 3. Save an entry
        int id = dao.addFuel(new FuelEntry(0, 4, LocalDate.of(2026, 11, 16), 18.4, 3.62));
        System.out.println(id > 0 ? "PASS addFuel returned id " + id : "FAIL addFuel");

        // 4. It reads back newest first with the saved total
        List<FuelEntry> v4 = dao.findByVehicle(4);
        FuelEntry first = v4.get(0);
        System.out.println(first.getFuelId() == id && Math.abs(first.getTotalCost() - 66.61) < 0.005
                ? "PASS saved total read back: " + first.getTotalCost()
                : "FAIL read back " + first.getTotalCost());

        // 5. Counts: V-004 had 5 entries, all vehicles had 45
        int all = dao.findAll().size();
        System.out.println(v4.size() == 6 && all == 46
                ? "PASS V-004 has " + v4Before + " entries, fleet has " + allBefore
                : "FAIL V-004 " + v4.size() + ", fleet " + all);
    }
}