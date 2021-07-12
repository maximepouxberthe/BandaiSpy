package com.skampe.bandaispy;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.skampe.utils.helpers.MySQLDatabaseHelper;
import com.skampe.utils.structures.MySQLDatabaseInstance;

public class BandaiSpySQLRequestHelper {

	private BandaiSpySQLRequestHelper() {
		// Hide public constructor
	}

	public static List<Object> getsMissingIconsId(final MySQLDatabaseInstance instance, final String server)
			throws SQLException {
		return MySQLDatabaseHelper.doSelectQuery(instance,
				String.format("SELECT * FROM `missingPictures` WHERE %s = true;", server), "id", String.class);
	}

	public static int getNbLinesInMissingPictures(final MySQLDatabaseInstance database) throws SQLException {
		return MySQLDatabaseHelper.doSelectQuery(database, "SELECT * FROM `missingPictures`;", "id", String.class)
				.size();
	}

	public static void setIconFound(final MySQLDatabaseInstance instance, final String server, final String id)
			throws SQLException {
		MySQLDatabaseHelper.doInsertOrUpdateOrDeleteQuery(instance,
				String.format("Update missingPictures set %s = false where id = '%s'", server, id));
	}

	public static void registerChannel(final MySQLDatabaseInstance instance, final String channelId,
			final String server, final String type) throws SQLException {
		MySQLDatabaseHelper.doInsertOrUpdateOrDeleteQuery(instance,
				String.format("Insert into registeredChannels VALUES ('%s', '%s', '%s')", channelId, server, type));
	}

	public static List<Object> getRegisteredChannelsForOverview(final MySQLDatabaseInstance instance,
			final String server) throws SQLException {
		return MySQLDatabaseHelper.doSelectQuery(instance,
				String.format("SELECT * FROM `registeredChannels` WHERE server = '%s' AND type = 'overviews';", server),
				"channelId", String.class);
	}

	public static List<Object> getRegisteredChannelsForImgurUrl(final MySQLDatabaseInstance instance,
			final String server) throws SQLException {
		return MySQLDatabaseHelper.doSelectQuery(instance, String
				.format("SELECT * FROM `registeredChannels` WHERE server = '%s' AND type = 'imgurAlbums';", server),
				"channelId", String.class);
	}

	public static List<Object> getRegisteredChannelsForIcons(final MySQLDatabaseInstance instance, final String server)
			throws SQLException {
		return MySQLDatabaseHelper.doSelectQuery(instance,
				String.format("SELECT * FROM `registeredChannels` WHERE server = '%s' AND type = 'icons';", server),
				"channelId", String.class);
	}

	public static List<Object> getMissingIconsUrlsForOverview(final MySQLDatabaseInstance instance, final String server)
			throws SQLException {
		final List<Object> result = new ArrayList<>();
		result.addAll(MySQLDatabaseHelper.doSelectQuery(instance,
				String.format("SELECT * FROM `iconsToDo` where server = '%s' AND iconId < 2000;", server), "iconUrl",
				String.class));
		result.addAll(MySQLDatabaseHelper.doSelectQuery(instance, String
				.format("SELECT * FROM `iconsToDo` where server = '%s' AND iconId between 10000 AND 13000;", server),
				"iconUrl", String.class));
		result.addAll(MySQLDatabaseHelper.doSelectQuery(instance, String
				.format("SELECT * FROM `iconsToDo` where server = '%s' AND iconId between 7000 AND 8000;", server),
				"iconUrl", String.class));
		result.addAll(MySQLDatabaseHelper.doSelectQuery(instance, String
				.format("SELECT * FROM `iconsToDo` where server = '%s' AND iconId between 9000 AND 10000;", server),
				"iconUrl", String.class));
		result.addAll(MySQLDatabaseHelper.doSelectQuery(instance, String
				.format("SELECT * FROM `iconsToDo` where server = '%s' AND iconId between 30000 AND 31000;", server),
				"iconUrl", String.class));
		result.addAll(MySQLDatabaseHelper.doSelectQuery(instance, String
				.format("SELECT * FROM `iconsToDo` where server = '%s' AND iconId between 59000 AND 60000;", server),
				"iconUrl", String.class));
		result.addAll(MySQLDatabaseHelper.doSelectQuery(instance, String
				.format("SELECT * FROM `iconsToDo` where server = '%s' AND iconId between 5000 AND 7000;", server),
				"iconUrl", String.class));
		result.addAll(MySQLDatabaseHelper.doSelectQuery(instance, String
				.format("SELECT * FROM `iconsToDo` where server = '%s' AND iconId between 15000 AND 18000;", server),
				"iconUrl", String.class));
		result.addAll(MySQLDatabaseHelper.doSelectQuery(instance, String
				.format("SELECT * FROM `iconsToDo` where server = '%s' AND iconId between 2000 AND 5000;", server),
				"iconUrl", String.class));
		result.addAll(MySQLDatabaseHelper.doSelectQuery(instance, String
				.format("SELECT * FROM `iconsToDo` where server = '%s' AND iconId between 8000 AND 9000;", server),
				"iconUrl", String.class));
		result.addAll(MySQLDatabaseHelper.doSelectQuery(instance, String
				.format("SELECT * FROM `iconsToDo` where server = '%s' AND iconId between 13000 AND 15000;", server),
				"iconUrl", String.class));
		result.addAll(MySQLDatabaseHelper.doSelectQuery(instance, String
				.format("SELECT * FROM `iconsToDo` where server = '%s' AND iconId between 18000 AND 30000;", server),
				"iconUrl", String.class));
		result.addAll(MySQLDatabaseHelper.doSelectQuery(instance, String
				.format("SELECT * FROM `iconsToDo` where server = '%s' AND iconId between 31000 AND 59000;", server),
				"iconUrl", String.class));
		return result;
	}

	public static String getImgurAlbumHash(final MySQLDatabaseInstance instance, final String server)
			throws SQLException {
		final List<Object> result = MySQLDatabaseHelper.doSelectQuery(instance,
				String.format("SELECT * FROM `lastImgurHash` WHERE server = '%s';", server), "albumHash", String.class);
		if (result.size() != 1) {
			return "";
		} else {
			return (String) result.get(0);
		}
	}

	public static void setImgurAlbumHash(final MySQLDatabaseInstance instance, final String server,
			final String albumHash) throws SQLException {
		MySQLDatabaseHelper.doInsertOrUpdateOrDeleteQuery(instance,
				String.format("Update lastImgurHash set albumHash = '%s' where server = '%s'", albumHash, server));
	}

	public static void initImgurAlbumHash(final MySQLDatabaseInstance instance, final String server)
			throws SQLException {
		MySQLDatabaseHelper.doInsertOrUpdateOrDeleteQuery(instance,
				String.format("Delete from lastImgurHash where server = '%s'", server));
		MySQLDatabaseHelper.doInsertOrUpdateOrDeleteQuery(instance,
				String.format("Insert into lastImgurHash VALUES ('%s', '%s')", server, ""));
	}

	public static void initMissingPictures(final MySQLDatabaseInstance instance) throws SQLException {
		final List<String> queries = new ArrayList<>();
		for (int i = 0; i < 60000; i++) {
			queries.add(String.format("Insert into missingPictures VALUES ('%s', 1, 1)", String.format("%04d", i)));
		}
		MySQLDatabaseHelper.doInsertOrUpdateOrDeleteQueries(instance, queries);
	}

	public static void cleanMissingPictures(final MySQLDatabaseInstance instance) throws SQLException {
		MySQLDatabaseHelper.doInsertOrUpdateOrDeleteQuery(instance,
				"Delete from missingPictures where glb = 0 AND jpn =0");

	}

	public static void unregisterChannel(final MySQLDatabaseInstance instance, final String channelId)
			throws SQLException {
		MySQLDatabaseHelper.doInsertOrUpdateOrDeleteQuery(instance,
				String.format("Delete from registeredChannels where channelId = %s", channelId));
	}

	public static void setIconToDo(final MySQLDatabaseInstance instance, final String server, final String iconUrl,
			final String iconId) throws SQLException {
		MySQLDatabaseHelper.doInsertOrUpdateOrDeleteQuery(instance,
				String.format("Insert into iconsToDo VALUES ('%s', '%s', '%s')", server, iconUrl, iconId));
	}

	public static int getNbIconsToDo(final MySQLDatabaseInstance instance, final String server) throws SQLException {
		return MySQLDatabaseHelper.doSelectQuery(instance,
				String.format("SELECT * FROM `iconsToDo` where server = '%s';", server), "server", String.class).size();
	}

	public static List<Object> getIconsUrlsToDo(final MySQLDatabaseInstance instance, final String server)
			throws SQLException {
		return MySQLDatabaseHelper.doSelectQuery(instance,
				String.format("SELECT * FROM `iconsToDo` where server = '%s';", server), "iconUrl", String.class);
	}

	public static void clearIconsToDo(final MySQLDatabaseInstance instance, final String server) throws SQLException {
		MySQLDatabaseHelper.doInsertOrUpdateOrDeleteQuery(instance,
				String.format("DELETE FROM `iconsToDo` where server = '%s';", server));
	}
}
