package com.skampe.utils.helpers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.skampe.utils.constants.CommonConstants;
import com.skampe.utils.exceptions.NotImplementedException;
import com.skampe.utils.structures.MySQLDatabaseInstance;

public class MySQLDatabaseHelper {

	private MySQLDatabaseHelper() {
		// Hide public constructor
	}

	public static void doInsertOrUpdateOrDeleteQuery(final MySQLDatabaseInstance instance, final String query)
			throws SQLException {
		final Connection conn = instance.getConnection();
		if (conn != null) {
			try (final PreparedStatement ps = conn.prepareStatement(query)) {
				ps.executeUpdate();
			} catch (final SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void doInsertOrUpdateOrDeleteQueries(final MySQLDatabaseInstance instance, final List<String> queries)
			throws SQLException {
		final Connection conn = instance.getConnection();
		if (conn != null) {
			for (final String query : queries) {
				try (final PreparedStatement ps = conn.prepareStatement(query)) {
					ps.executeUpdate();
				} catch (final SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static <T> List<Object> doSelectQuery(final MySQLDatabaseInstance instance, final String query,
			final String column, final Class<T> columnType) throws SQLException {
		final List<Object> returnValues = new ArrayList<>();
		final Connection conn = instance.getConnection();
		if (conn != null) {
			try (PreparedStatement ps = conn.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
				switch (columnType.getName()) {
				case CommonConstants.STRING_CLASS_NAME:
					while (rs.next()) {
						returnValues.add(rs.getString(column));
					}
					break;
				case CommonConstants.INTEGER_CLASS_NAME:
					while (rs.next()) {
						returnValues.add(rs.getInt(column));
					}
					break;
				case CommonConstants.LONG_CLASS_NAME:
					while (rs.next()) {
						returnValues.add(rs.getLong(column));
					}
					break;
				default:
					throw new NotImplementedException(
							String.format("%s type is not implemented right now.", columnType.getName()));
				}

			}
		}
		return returnValues;
	}
}
