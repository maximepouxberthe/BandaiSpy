package com.skampe.bandaispy;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.security.auth.login.LoginException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.javatuples.KeyValue;
import org.quartz.SchedulerException;

import com.skampe.utils.api.helpers.TwitterHelper;
import com.skampe.utils.helpers.ConstantsHelper;
import com.skampe.utils.helpers.SchedulerHelper;
import com.skampe.utils.helpers.discord.DiscordBotHelper;
import com.skampe.utils.structures.DiscordCommand;
import com.skampe.utils.structures.MySQLDatabaseInstance;

import net.dv8tion.jda.api.Permission;
import twitter4j.Twitter;

public class Main {

	private static final Logger LOGGER = LogManager.getLogger(Main.class);

	public static void main(final String[] args) {
		if (initConstants() && initScheduler() && initDatabase() && initBot()) {
			initTwitter();
			launchBandaiSpy();
			shutdownScheduler();
		}
	}

	private static boolean initConstants() {
		LOGGER.info("Initiating constants");
		final List<KeyValue<String, Boolean>> properties = new ArrayList<>();
		properties.add(new KeyValue<>(ConstantsProperties.DATABASE_URL_PROPERTY, true));
		properties.add(new KeyValue<>(ConstantsProperties.DATABASE_USERNAME_PROPERTY, true));
		properties.add(new KeyValue<>(ConstantsProperties.DATABASE_PASSWORD_PROPERTY, true));
		properties.add(new KeyValue<>(ConstantsProperties.NB_REQUEST_THREADS_PROPERTY, true));
		properties.add(new KeyValue<>(ConstantsProperties.IMGUR_CLIENT_ID_PROPERTY, true));
		properties.add(new KeyValue<>(ConstantsProperties.MAX_EXPECTED_DURATION_MILLIS_PROPERTY, true));
		properties.add(new KeyValue<>(ConstantsProperties.BOT_TOKEN_PROPERTY, true));
		try {
			ConstantsHelper.initConstants(properties, BandaiSpyConstants.PROPERTIES_FILE);
			LOGGER.info("Constants initiated");
			return true;
		} catch (final IOException e) {
			LOGGER.error("Failed to initiate constants", e);
			return false;
		}
	}

	private static boolean initScheduler() {
		LOGGER.info("Initiating scheduler");
		try {
			SchedulerHelper.initScheduler();
			LOGGER.info("Scheduler initiated");
			return true;
		} catch (final SchedulerException e) {
			LOGGER.error("Failed to initiate scheduler", e);
			return false;
		}
	}

	private static boolean shutdownScheduler() {
		LOGGER.info("Shutting down scheduler");
		try {
			SchedulerHelper.shutdownScheduler();
			LOGGER.info("Scheduler shutted down");
			return true;
		} catch (final SchedulerException e) {
			LOGGER.error("Failed to shut down scheduler", e);
			return false;
		}
	}

	private static boolean initDatabase() {
		LOGGER.info("Initiating the database");
		final MySQLDatabaseInstance instance = new MySQLDatabaseInstance(
				ConstantsHelper.get(ConstantsProperties.DATABASE_URL_PROPERTY, String.class),
				ConstantsHelper.get(ConstantsProperties.DATABASE_USERNAME_PROPERTY, String.class),
				ConstantsHelper.get(ConstantsProperties.DATABASE_PASSWORD_PROPERTY, String.class));
		BandaiSpyHelper.setDatabase(instance);
		if (BandaiSpyHelper.fillDatabaseIfEmpty()) {
			LOGGER.info("Database initiated");
			return true;
		} else {
			LOGGER.error("Failed to initiate the database");
			return false;
		}
	}

	private static void initTwitter() {
		LOGGER.info("Initiating the Twitter api");
		final Twitter instance = TwitterHelper.buildTwitterInstance("vRzJIT4sxjpJ8IaWk2fgHQjPG",
				"4VkzFLfwPMLboceWAA84NjKWXidYzzfG5uxaYIGjaFM2t5CJEN",
				"1406567935980810243-mF1KGf4N7MD6oeIrUHbZo1nB7tluUV", "TfPlqn1Um4HeBg9C3o0EOzKRcfxICmcDDUyH8bDIjEEyV");
		BandaiSpyHelper.setTwitterBot(instance);
		LOGGER.info("Twitter api initiated");
	}

	private static boolean initBot() {
		LOGGER.info("Initiating discord bot");
		final List<DiscordCommand> commands = new ArrayList<>();
		commands.add(
				new DiscordCommand("$bandaiSpy registerChannel", 2, DiscordRegisterChannelCommandTask.class.getName(),
						Arrays.asList(), Arrays.asList(Permission.MANAGE_SERVER)));

		final BandaiSpyDiscordBot discordBot = new BandaiSpyDiscordBot(commands);
		try {
			DiscordBotHelper.buildBot(discordBot,
					ConstantsHelper.get(ConstantsProperties.BOT_TOKEN_PROPERTY, String.class));
			BandaiSpyHelper.setDiscordBot(discordBot);
			LOGGER.info("Discord bot initiated");
			return true;
		} catch (final LoginException e) {
			LOGGER.error("Failed to shut down scheduler", e);
			return false;
		}
	}

	private static void launchBandaiSpy() {
		try {
			List<String> urlsGlb = BandaiSpyHelper.getUrlsOfMissingIcons(BandaiSpyConstants.GLB_SERVER);
			List<String> urlsJpn = BandaiSpyHelper.getUrlsOfMissingIcons(BandaiSpyConstants.JPN_SERVER);
			do {
				LOGGER.info("Launching the worker for both servers");
				launchWorker(Stream.concat(urlsGlb.stream(), urlsJpn.stream()).collect(Collectors.toList()), true);

				if (BandaiSpyHelper.countIconsToDo(BandaiSpyConstants.JPN_SERVER) != 0) {
					LOGGER.info(String.format("Icons were found on %s. Launching the worker for %s server",
							BandaiSpyConstants.JPN_SERVER, BandaiSpyConstants.JPN_SERVER));
					launchWorker(BandaiSpyHelper.getUrlsOfMissingIcons(BandaiSpyConstants.JPN_SERVER), false);
					BandaiSpyHelper.publish(BandaiSpyConstants.JPN_SERVER);
				}

				if (BandaiSpyHelper.countIconsToDo(BandaiSpyConstants.GLB_SERVER) != 0) {
					LOGGER.info(String.format("Icons were found on %s. Launching the worker for %s server",
							BandaiSpyConstants.GLB_SERVER, BandaiSpyConstants.GLB_SERVER));
					launchWorker(BandaiSpyHelper.getUrlsOfMissingIcons(BandaiSpyConstants.GLB_SERVER), false);
					BandaiSpyHelper.publish(BandaiSpyConstants.GLB_SERVER);
				}

				urlsGlb = BandaiSpyHelper.getUrlsOfMissingIcons(BandaiSpyConstants.GLB_SERVER);
				urlsJpn = BandaiSpyHelper.getUrlsOfMissingIcons(BandaiSpyConstants.JPN_SERVER);
			} while (!urlsGlb.isEmpty() && !urlsJpn.isEmpty());
		} catch (final SQLException e) {
			LOGGER.error("Failed to launch BandaiSpy", e);
		}
	}

	private static void launchWorker(final List<String> urls, final boolean allowStopBuckets) {
		LOGGER.info(String.format("Launching the worker for %s urls", urls.size()));
		final HttpRequestsWorker worker = new HttpRequestsWorker(
				ConstantsHelper.get(ConstantsProperties.NB_REQUEST_THREADS_PROPERTY, Integer.class), urls,
				allowStopBuckets);
		worker.work();
		try {
			worker.awaitTerminationOrForceStop(
					ConstantsHelper.get(ConstantsProperties.MAX_EXPECTED_DURATION_MILLIS_PROPERTY, Integer.class));
		} catch (final InterruptedException e) {
			LOGGER.error("Failed to launch worker", e);
		}
	}
}
