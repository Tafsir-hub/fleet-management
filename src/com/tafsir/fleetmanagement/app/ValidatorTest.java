package com.tafsir.fleetmanagement.app;

import com.tafsir.fleetmanagement.logic.Validator;
import com.tafsir.fleetmanagement.model.VehicleStatus;

public class ValidatorTest {

	public static void main(String[] args) {
		Validator v = new Validator();
		
		check("TC-01 all fields filled", true, v.requireFields("Ford Transit", "2022", "Van", "10000"));
		check("TC-02 year missing", false, v.requireFields("Ford Transit", "", "Van", "10000"));
		check("TC-03 mileage goes up", true, v.validMileage(10000,10500));
		check("TC-04 mileage goes down", false, v.validMileage(10000,9000));
		check("TC-05 In repair selected", true, v.validStatus(VehicleStatus.IN_REPAIR));
		

	}

	private static void check(String name, Object expected, Object actual) {
		String result = expected.equals(actual) ? "PASS" : "FAIL";
		System.out.println(result + " - " + name + "(expected " + expected + ", got " + actual + ")");
		
	}

}
