package com.tafsir.fleetmanagement.model;

import java.time.LocalDate;

public class Driver {
	
	private int driverId;
	private String firstName;
	private String lastName;
	private String licenseType;
	private LocalDate hireDate;
	private DriverStatus status;
	
	
	public Driver() {
		
	}
	
	
	
	public Driver(int driverId, String firstName, String lastName, String licenseType, LocalDate hireDate,
			DriverStatus status) {
		super();
		this.driverId = driverId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.licenseType = licenseType;
		this.hireDate = hireDate;
		this.status = status;
	}
	
	
	public int getDriverId() {
		return driverId;
	}



	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}



	public String getFirstName() {
		return firstName;
	}



	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}



	public String getLastName() {
		return lastName;
	}



	public void setLastName(String lastName) {
		this.lastName = lastName;
	}



	public String getLicenseType() {
		return licenseType;
	}



	public void setLicenseType(String licenseType) {
		this.licenseType = licenseType;
	}



	public LocalDate getHireDate() {
		return hireDate;
	}



	public void setHireDate(LocalDate hireDate) {
		this.hireDate = hireDate;
	}



	public DriverStatus getStatus() {
		return status;
	}



	public void setStatus(DriverStatus status) {
		this.status = status;
	}



	public String getFullName() {
		return firstName + " " + lastName;
	}
	
	public String getShortName() {
		return firstName.charAt(0) + ". " + lastName;
	}

}
