package com.tafsir.fleetmanagement.app;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

import com.tafsir.fleetmanagement.dao.DBConnection;
import com.tafsir.fleetmanagement.logic.PasswordHasher;

// Creates a login account. Developer tool, not part of the product.
//>> CHANGE: it now SAVES the account itself. The hash goes straight from PasswordHasher into the database,
//>> so nothing is copied by hand and nothing extra can sneak into the stored text.
//>> It extends DBConnection only to borrow getConnection(), the same login the app uses (config.properties).
public class CreateUserTool extends DBConnection {

    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            System.out.print("Username: ");
            String username = in.nextLine().trim();
            System.out.print("Password: ");
            String password = in.nextLine();
            if (username.isEmpty() || password.isEmpty()) {
                System.out.println("Both are required. Run the tool again.");
                return;
            }
            new CreateUserTool().insert(username, PasswordHasher.hash(password));
            System.out.println("Account created: " + username);
        } catch (SQLException e) {
            //>> A second account with the same username lands here ("Duplicate entry"): the unique rule is doing its job.
            System.out.println("Could not create the account: " + e.getMessage());
        }
    }

    private void insert(String username, String hash) throws SQLException {
        //>> The ? placeholders pass the values as plain data. The role is fixed: there is one role for now.
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, 'FLEET_MANAGER')";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.executeUpdate();
        }
    }
}