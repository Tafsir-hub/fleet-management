package com.tafsir.fleetmanagement.app;

import java.sql.SQLException;
import java.util.Scanner;
import com.tafsir.fleetmanagement.logic.Authenticator;

//>> Checks TC-27 and TC-28 against the real database. TC-29 (logout) is a screen action, so we test it on the screen in step 3.
public class LoginTest {

    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            System.out.print("Username of the account you created: ");
            String user = in.nextLine().trim();
            System.out.print("Its password: ");
            String pass = in.nextLine();

            Authenticator auth = new Authenticator();
            check("TC-27 valid login", true, auth.isValidLogin(user, pass));
            check("TC-28 wrong password", false, auth.isValidLogin(user, pass + "x"));   //>> the right user with a wrong password
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void check(String name, Object expected, Object actual) {
        String result = expected.equals(actual) ? "PASS" : "FAIL";
        System.out.println(result + " - " + name + " (expected " + expected + ", got " + actual + ")");
    }
}
