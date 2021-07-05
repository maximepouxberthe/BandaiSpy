package com.skampe.utils.structures;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;

public class MySQLDatabaseInstance {

	private static final Logger LOGGER = LogManager.getLogger(MySQLDatabaseInstance.class);

	private Connection connection = null;
	private String databaseUrl = "";
	private String databaseUsername = "";
	private String databasePassword = "";

	public MySQLDatabaseInstance(final String dbUrl, final String dbUsername, final String dbPassword) {
		databaseUrl = dbUrl;
		databaseUsername = dbUsername;
		databasePassword = dbPassword;
	}

	private void connect(final int nbRetries) throws SQLException {
		try {
			connection = DriverManager.getConnection(databaseUrl, databaseUsername, databasePassword);
		} catch (final CommunicationsException e) {
			LOGGER.warn(String.format("Failed to connect to DB. nbRetries: %s", nbRetries));
			if (nbRetries < 50) {
				try {
					Thread.sleep((long) 100 * nbRetries);
				} catch (final InterruptedException e1) {
					e1.printStackTrace();
				}
				connect(nbRetries + 1);
			}
		}
	}

	public Connection getConnection() throws SQLException {
		if (connection == null || connection.isClosed() || !connection.isValid(1000)) {
			if (connection != null) {
				connection.close();
			}
			connect(0);
		}
		return connection;
	}

}
