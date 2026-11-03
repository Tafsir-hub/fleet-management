package com.tafsir.fleetmanagement.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import com.tafsir.fleetmanagement.model.User;

public class UserDAO extends DBConnection{
	
	// This returns the user with that username, or empty if there is none
	public Optional<User> findByUsername(String username) throws SQLException {
		String sql = "SELECT user_id, username, password_hash, role FROM users WHERE username = ?";
		try (Connection conn = getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, username);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(new User(
							rs.getInt("user_id"),
							rs.getString("username"),
							rs.getString("password_hash"),
							rs.getString("role")));
							
				}
				
			}
					
		}
		return Optional.empty();
	}

}
