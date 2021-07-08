package com.skampe.bandaispy;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.skampe.utils.api.helpers.HtmlCssToImageHelper;
import com.skampe.utils.api.helpers.ImgurHelper;
import com.skampe.utils.api.helpers.TwitterHelper;
import com.skampe.utils.bots.DiscordBot;
import com.skampe.utils.constants.HttpStatusConstants;
import com.skampe.utils.constants.ImgurConstants;
import com.skampe.utils.constants.OkHttpConstants;
import com.skampe.utils.helpers.ConstantsHelper;
import com.skampe.utils.helpers.FileHelper;
import com.skampe.utils.helpers.JacksonJsonHelper;
import com.skampe.utils.helpers.OkHttpHelper;
import com.skampe.utils.helpers.discord.DiscordBotHelper;
import com.skampe.utils.structures.MySQLDatabaseInstance;

import okhttp3.Response;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class BandaiSpyHelper {

	private static final Logger LOGGER = LogManager.getLogger(BandaiSpyHelper.class);

	private static MySQLDatabaseInstance database;
	private static DiscordBot discordBot;
	private static Twitter twitterBot;

	private BandaiSpyHelper() {
		// Hide public constructor
	}

	public static void setDatabase(final MySQLDatabaseInstance instance) {
		database = instance;
	}

	public static void setDiscordBot(final DiscordBot bot) {
		discordBot = bot;
	}

	public static void setTwitterBot(final Twitter instance) {
		twitterBot = instance;
	}

	public static List<String> getUrlsOfMissingIcons(final String server) throws SQLException {
		String url = "";
		if (BandaiSpyConstants.GLB_SERVER.equals(server)) {
			url = BandaiSpyConstants.GLB_URL;
		} else if (BandaiSpyConstants.JPN_SERVER.equals(server)) {
			url = BandaiSpyConstants.JPN_URL;
		} else {
			LOGGER.error(String.format("Unknow server : '%s'. Allowed values : %s", server,
					BandaiSpyConstants.SERVERS_LIST));
			return new ArrayList<>();
		}

		LOGGER.info("Fetching missing icons from DB");
		final List<String> missingIconsUrls = new ArrayList<>();

		final List<Object> missingIconsIds = BandaiSpySQLRequestHelper.getsMissingIconsId(database, server);

		LOGGER.info(String.format("Found %s missing icons on %s", missingIconsIds.size(), server));

		for (final Object missingIconId : missingIconsIds) {
			missingIconsUrls.add(String.format(url, missingIconId));
		}
		return missingIconsUrls;
	}

	private static String createNewImgurAlbum(final String version) {
		LOGGER.info(String.format("Creating new album for %s version", version));
		final ObjectMapper mapper = new ObjectMapper();
		JsonNode album = mapper.createObjectNode();
		try {
			album = ImgurHelper.createAlbum(
					ConstantsHelper.get(ConstantsProperties.IMGUR_CLIENT_ID_PROPERTY, String.class),
					String.format("BandaiSpy %s %s", version.toUpperCase(), LocalDateTime.now()));
		} catch (final IOException e) {
			LOGGER.error(String.format("Failed to create a new imgur album for version %s", version), e);
			return "";
		}

		if (JacksonJsonHelper.getInt(album, "status",
				HttpStatusConstants.UNEXISTANT_SC) == HttpStatusConstants.SC_200_OK) {
			final JsonNode data = JacksonJsonHelper.getJsonNode(album, ImgurConstants.JSON_RESPONSE_DATA_KEY);
			final String albumHash = JacksonJsonHelper.getString(data, ImgurConstants.JSON_RESPONSE_DELETEHASH_KEY);
			final String albumId = JacksonJsonHelper.getString(data, ImgurConstants.JSON_RESPONSE_ID_KEY);

			if (StringUtils.isNotEmpty(albumHash)) {
				try {
					BandaiSpySQLRequestHelper.setImgurAlbumHash(database, version, albumHash);
				} catch (final SQLException e) {
					LOGGER.error(String.format(
							"Failed to set albumHash for version %s in database. Icons won't be uploaded in the right imgur album",
							version), e);
				}
			} else {
				LOGGER.error(
						String.format("Empty albumHash. Error while creating imgur album for %s version", version));
			}
			if (StringUtils.isNotEmpty(albumId)) {
				final String albumUrl = String.format("https://imgur.com/a/%s", albumId);
				LOGGER.info(String.format("New album created for %s version: %s", version, albumUrl));
				return albumUrl;
			} else {
				LOGGER.error(String.format("Empty albumId. Error while creating imgur album for %s version", version));
				return "";
			}
		} else {
			return "";
		}
	}

	public static void setNewIconToDo(final String iconUrl) {
		final String version = getVersion(iconUrl);
		final String iconId = getId(version, iconUrl);

		if (!StringUtils.isEmpty(version) && !StringUtils.isBlank(iconId)) {
			try {
				BandaiSpySQLRequestHelper.setIconToDo(database, version, iconUrl, iconId);
				updateDatabase(version, iconId);
			} catch (final SQLException e) {
				LOGGER.error(String.format("Failed to set iconToDo for url '%s', server '%s', id '%s'", iconUrl,
						version, iconId), e);
			}
		} else {
			LOGGER.error(String.format("Failed to find version and id for urls '%s'", iconUrl));
		}
	}

	public static int countIconsToDo(final String server) {
		try {
			return BandaiSpySQLRequestHelper.getNbIconsToDo(database, server);
		} catch (final SQLException e) {
			LOGGER.error(String.format("Failed to count iconToDo for server '%s'", server), e);
			return 0;
		}
	}

	public static void publish(final String server) {
		LOGGER.info(String.format("Publishing icons for %s server", server));
		final String imgurUrl = createNewImgurAlbum(server);
		String overviewUrl = "";
		try {
			overviewUrl = HtmlCssToImageHelper.buildImage(buildHtmlOverview(server), BandaiSpyConstants.OVERVIEW_CSS);
		} catch (IOException | SQLException e) {
			LOGGER.error("Failed to build overview", e);
		}
		LOGGER.info(String.format("Overview for %s version generated at %s", server, overviewUrl));
		byte[] overview = null;
		try {
			final Response response = OkHttpHelper.executeRequest(
					OkHttpHelper.createRequest().withUrl(overviewUrl).withMethod(OkHttpConstants.METHOD_GET).build());
			if (OkHttpHelper.getHttpCodeFromResponse(response) == HttpStatusConstants.SC_200_OK) {
				overview = OkHttpHelper.getBodyFromResponseAsBytes(response);
				if (FileHelper.isValidPNG(overview)) {
					LOGGER.debug("Got overview data from url");
					publishOverview(overviewUrl, overview, server);
				}
			}
			response.close();
		} catch (final IOException e) {
			LOGGER.error("Failed to get overview data from overview url", e);
		}

		if (!StringUtils.isBlank(imgurUrl)) {
			publishImgurUrl(server, imgurUrl);
		}
		publishTweetWithOverviewAndAlbumUrl(overview, imgurUrl, server);

		try {
			for (final Object iconUrlObj : BandaiSpySQLRequestHelper.getIconsUrlsToDo(database, server)) {
				final String iconUrl = (String) iconUrlObj;
				final Response response = OkHttpHelper.executeRequest(
						OkHttpHelper.createRequest().withUrl(iconUrl).withMethod(OkHttpConstants.METHOD_GET).build());
				if (OkHttpHelper.getHttpCodeFromResponse(response) == HttpStatusConstants.SC_200_OK) {
					final byte[] data = OkHttpHelper.getBodyFromResponseAsBytes(response);
					if (FileHelper.isValidPNG(data)) {
						publishIcon(server, data, iconUrl);
					}
				}
				response.close();
			}
			BandaiSpySQLRequestHelper.clearIconsToDo(database, server);
		} catch (final SQLException | IOException e) {
			LOGGER.error("Failed to publish one or more icons", e);
		}
	}

	private static void publishTweetWithOverviewAndAlbumUrl(final byte[] overview, final String imgurUrl,
			final String version) {

		try {
			if (StringUtils.isBlank(imgurUrl)) {
				if (overview == null || overview.length == 0) {
					TwitterHelper.sendTweet(twitterBot, String.format(
							"There are new icons on %s, but the bot encountered an error. Failed to send overview and imgur link.",
							version));
				} else {
					TwitterHelper.sendTweet(twitterBot,
							String.format("New icons on %s!%nFailed to send imgur link.", version), "overview.png",
							overview);
				}
			} else {
				if (overview == null || overview.length == 0) {
					TwitterHelper.sendTweet(twitterBot, String
							.format("New icons on %s!%nFailed to send overview.%nImgur album: %s", version, imgurUrl));
				} else {
					TwitterHelper.sendTweet(twitterBot,
							String.format("New icons on %s!%nImgur album: %s", version, imgurUrl), "overview.png",
							overview);
				}
			}
		} catch (final TwitterException e) {
			LOGGER.error("Failed to publish tweet", e);
		}
	}

	private static void publishImgurUrl(final String version, final String imgurUrl) {
		publishImgurUrlToDiscordRegisteredChannels(imgurUrl, version);
	}

	private static void publishImgurUrlToDiscordRegisteredChannels(final String imgurUrl, final String version) {
		try {
			for (final Object channelId : BandaiSpySQLRequestHelper.getRegisteredChannelsForImgurUrl(database,
					version)) {
				DiscordBotHelper.sendMessage(discordBot, (String) channelId, String.format("<%s>", imgurUrl));
			}
		} catch (final SQLException e) {
			LOGGER.error("Failed to get registered discord channels for imgur urls from DB", e);
		}
	}

	private static void publishOverview(final String overviewUrl, final byte[] overview, final String version) {
		LOGGER.debug(String.format("Sending overview for %s", version));
		publishOverviewToDiscordRegisteredChannels(overview, version);
		addOverviewToImgurAlbum(overviewUrl, version);
	}

	private static void publishOverviewToDiscordRegisteredChannels(final byte[] overview, final String version) {
		LOGGER.debug(String.format("Sending overview for %s server to discord channels", version));
		try {
			for (final Object channelId : BandaiSpySQLRequestHelper.getRegisteredChannelsForOverview(database,
					version)) {
				DiscordBotHelper.sendMessage(discordBot, (String) channelId, overview,
						String.format("%s_Overview.png", version.toUpperCase()));
			}
		} catch (final SQLException e) {
			LOGGER.error("Failed to get registered discord channels for overviews from DB", e);
		}
	}

	private static void publishIconToDiscordRegisteredChannels(final byte[] icon, final String version) {
		try {
			for (final Object channelId : BandaiSpySQLRequestHelper.getRegisteredChannelsForIcons(database, version)) {
				DiscordBotHelper.sendMessage(discordBot, (String) channelId, icon, "icon.png");
			}
		} catch (final SQLException e) {
			LOGGER.error("Failed to get registered discord channels for icons from DB", e);
		}
	}

	private static void publishIcon(final String version, final byte[] icon, final String iconUrl) {
		publishIconToDiscordRegisteredChannels(icon, version);
		addIconToImgurAlbum(version, iconUrl);
	}

	private static void addOverviewToImgurAlbum(final String overviewUrl, final String version) {
		LOGGER.debug(String.format("Sending overview for %s version to imgur album", version));
		try {
			final String albumHash = BandaiSpySQLRequestHelper.getImgurAlbumHash(database, version);
			if (!StringUtils.isBlank(albumHash)) {
				ImgurHelper.addImageToAlbumFromUrl(
						ConstantsHelper.get(ConstantsProperties.IMGUR_CLIENT_ID_PROPERTY, String.class), albumHash,
						overviewUrl);
			}
		} catch (final SQLException | IOException e) {
			LOGGER.error("Failed to add overview to imgur album", e);
		}

	}

	private static void addIconToImgurAlbum(final String version, final String iconUrl) {

		try {
			final String albumHash = BandaiSpySQLRequestHelper.getImgurAlbumHash(database, version);
			if (!StringUtils.isBlank(albumHash)) {
				ImgurHelper.addImageToAlbumFromUrl(
						ConstantsHelper.get(ConstantsProperties.IMGUR_CLIENT_ID_PROPERTY, String.class), albumHash,
						iconUrl);
			}
		} catch (final SQLException | IOException e) {
			LOGGER.error("Failed to add icon to imgur album", e);
		}

	}

	private static void updateDatabase(final String version, final String iconId) {
		try {
			BandaiSpySQLRequestHelper.setIconFound(database, version, iconId);
		} catch (final SQLException e) {
			LOGGER.error(String.format("Failed to update database for icon %s on %s:", iconId, version));
			LOGGER.error(e);
		}
	}

	private static String getId(final String version, final String url) {
		String id = "";
		switch (version) {
		case BandaiSpyConstants.GLB_SERVER:
			id = url.substring("https://cdn.gb.onepiece-tc.jp/fr/images/characters/character_".length())
					.replace("_t.png", "");
			break;
		case BandaiSpyConstants.JPN_SERVER:
			id = url.substring("https://cdn.onepiece-tc.jp/images/characters/character_".length()).replace("_t.png",
					"");
			break;
		default:
			LOGGER.error("Unknown version: %s", version);
		}
		if (StringUtils.isBlank(id)) {
			LOGGER.error(String.format("Failed to get icon id from url %s", url));
		} else {
			LOGGER.debug(String.format("Icon id found for %s: %s", url, id));
		}
		return id;
	}

	private static String getVersion(final String url) {
		if (url.startsWith("https://cdn.gb.onepiece-tc.jp/fr/images/characters/character_")) {
			return BandaiSpyConstants.GLB_SERVER;
		} else if (url.startsWith("https://cdn.onepiece-tc.jp/images/characters/character_")) {
			return BandaiSpyConstants.JPN_SERVER;
		} else {
			return "";
		}
	}

	public static String buildHtmlOverview(final String version) throws SQLException {

		LOGGER.debug(String.format("Starting the html generation for the overview for %s version", version));
		final List<Object> missingUrls = BandaiSpySQLRequestHelper.getMissingIconsUrlsForOverview(database, version);
		final StringBuilder builder = new StringBuilder();
		builder.append("<body>");
		builder.append("<center>");
		builder.append("<div>");
		builder.append("<p>Source : Mask, Anlord</p>");

		for (final Object missingUrl : missingUrls) {
			builder.append(String.format("<img src=\"%s\" onerror=\"this.style.display='none'\" >", missingUrl));
		}

		builder.append("</div>");
		builder.append("</center>");
		builder.append("</body>");

		LOGGER.debug(String.format("Html for the overview for %s version generated", version));

		return builder.toString();
	}

	public static void registerChannel(final String channelId, final String version, final String type) {
		try {
			BandaiSpySQLRequestHelper.registerChannel(database, channelId, version, type);
		} catch (final SQLException e) {
			LOGGER.error("Failed to register channel");
			LOGGER.error(e);
		}
	}

	public static boolean fillDatabaseIfEmpty() {
		try {

			if (StringUtils
					.isEmpty(BandaiSpySQLRequestHelper.getImgurAlbumHash(database, BandaiSpyConstants.JPN_SERVER))) {
				BandaiSpySQLRequestHelper.initImgurAlbumHash(database, BandaiSpyConstants.JPN_SERVER);
			}

			if (StringUtils
					.isEmpty(BandaiSpySQLRequestHelper.getImgurAlbumHash(database, BandaiSpyConstants.GLB_SERVER))) {
				BandaiSpySQLRequestHelper.initImgurAlbumHash(database, BandaiSpyConstants.GLB_SERVER);
			}
			if (BandaiSpySQLRequestHelper.getNbLinesInMissingPictures(database) == 0) {
				BandaiSpySQLRequestHelper.initMissingPictures(database);
			} else {
				BandaiSpySQLRequestHelper.cleanMissingPictures(database);
			}
			return true;
		} catch (final SQLException e) {
			LOGGER.error("Failed to fill the database", e);
			return false;
		}
	}
}
