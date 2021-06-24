package com.skampe.utils.structures;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabaseInstance {

	private static Connection connection = null;
	private static String databaseUrl = "";
	private static String databaseUsername = "";
	private static String databasePassword = "";

	public MySQLDatabaseInstance(final String dbUrl, final String dbUsername, final String dbPassword) {
		databaseUrl = dbUrl;
		databaseUsername = dbUsername;
		databasePassword = dbPassword;
	}

	private void connect() throws SQLException {
		connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
	}

	public Connection getConnection() throws SQLException {
		if (connection == null || !connection.isValid(1000)) {
			if (connection != null) {
				connection.close();
			}
			connect();
		}
		return connection;
	}

}
