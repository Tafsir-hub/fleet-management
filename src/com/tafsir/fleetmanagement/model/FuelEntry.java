package com.tafsir.fleetmanagement.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class FuelEntry {
    private int fuelId;
    private int vehicleId;
    private LocalDate fuelDate;
    private double gallons;
    private double pricePerGallon;
    private double totalCost;

    // New entry: the total is worked out here
    public FuelEntry(int fuelId, int vehicleId, LocalDate fuelDate,
                     double gallons, double pricePerGallon) {
        this(fuelId, vehicleId, fuelDate, gallons, pricePerGallon,
                calculateTotal(gallons, pricePerGallon));
    }

    public int getFuelId() {
		return fuelId;
	}

	public void setFuelId(int fuelId) {
		this.fuelId = fuelId;
	}

	public int getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(int vehicleId) {
		this.vehicleId = vehicleId;
	}

	public LocalDate getFuelDate() {
		return fuelDate;
	}

	public void setFuelDate(LocalDate fuelDate) {
		this.fuelDate = fuelDate;
	}

	public double getGallons() {
		return gallons;
	}

	public void setGallons(double gallons) {
		this.gallons = gallons;
	}

	public double getPricePerGallon() {
		return pricePerGallon;
	}

	public void setPricePerGallon(double pricePerGallon) {
		this.pricePerGallon = pricePerGallon;
	}

	public double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}

	// Entry read back from the database: the saved total is used as stored
    public FuelEntry(int fuelId, int vehicleId, LocalDate fuelDate,
                     double gallons, double pricePerGallon, double totalCost) {
        this.fuelId = fuelId;
        this.vehicleId = vehicleId;
        this.fuelDate = fuelDate;
        this.gallons = gallons;
        this.pricePerGallon = pricePerGallon;
        this.totalCost = totalCost;
    }

    // gallons x price, rounded half-up to the cent
    public static double calculateTotal(double gallons, double pricePerGallon) {
        return BigDecimal.valueOf(gallons)
                .multiply(BigDecimal.valueOf(pricePerGallon))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    
}