package com.tafsir.fleetmanagement.logic;

import java.sql.SQLException;
import java.util.Optional;

import com.tafsir.fleetmanagement.dao.UserDAO;
import com.tafsir.fleetmanagement.model.User;

public class Authenticator {
	
	private final UserDAO userDAO = new UserDAO();
	
	// True only if the username exists and the password matches.
	public boolean isValidLogin(String username, String password) throws SQLException {
		if(username == null || username.trim().isEmpty() || password.isEmpty()) {
			return false;
		}
		Optional<User> found = userDAO.findByUsername(username.trim());
		return found.isPresent() && PasswordHasher.verify(password, found.get().getPasswordHash());
	}

}
