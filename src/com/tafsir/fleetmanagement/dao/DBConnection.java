package com.tafsir.fleetmanagement.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;



public abstract class DBConnection {
	
	private final static String CONFIG_FILE = "config.properties";
	
	
	protected Connection getConnection() throws SQLException {
		Properties config = new Properties();
		try(FileInputStream in = new FileInputStream(CONFIG_FILE)){
			config.load(in);
		} catch (IOException e) {
			throw new SQLException("Could not read "+ CONFIG_FILE +" Is it in the project folder?",e);
		}
		return DriverManager.getConnection(
		config.getProperty("db.url"),
		config.getProperty("db.user"),
		config.getProperty("db.password"));
		
				
		
	}
}
