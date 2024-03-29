package com.skampe.utils.structures;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

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
			if (nbRetries < 50) {
				LOGGER.info(String.format("Failed to connect to DB. nbRetries: %s", nbRetries));
			} else {
				final long dbUnavailableTimeMillis = 127500 + (nbRetries - 50) * 5000;
				LOGGER.warn(String.format("Failed to connect to DB. nbRetries: %s. BB is unavailable since %s",
						nbRetries,
						String.format("%02d min, %02d sec", TimeUnit.MILLISECONDS.toMinutes(dbUnavailableTimeMillis),
								TimeUnit.MILLISECONDS.toSeconds(dbUnavailableTimeMillis) - TimeUnit.MINUTES
										.toSeconds(TimeUnit.MILLISECONDS.toMinutes(dbUnavailableTimeMillis)))));
			}
			try {
				Thread.sleep(Math.min(5000, (long) 100 * nbRetries));
				connect(nbRetries + 1);
			} catch (final InterruptedException e1) {
				LOGGER.error("Failed to wait to reconnect later to DB.", e);
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
