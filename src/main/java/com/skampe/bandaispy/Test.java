package com.skampe.bandaispy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.javatuples.KeyValue;
import org.quartz.SchedulerException;

import com.skampe.utils.helpers.ConstantsHelper;
import com.skampe.utils.helpers.SchedulerHelper;
import com.skampe.utils.structures.MySQLDatabaseInstance;

public class Test {
	public static void main(final String[] args) {

		initConstants();
		initScheduler();
		final MySQLDatabaseInstance database = initDatabase();

		BandaiSpyHelper.createNewImgurAlbum("glb");

	}

	private static void initConstants() {
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
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	private static void initScheduler() {
		try {
			SchedulerHelper.initScheduler();
		} catch (final SchedulerException e) {
			e.printStackTrace();
		}
	}

	private static MySQLDatabaseInstance initDatabase() {
		final MySQLDatabaseInstance instance = new MySQLDatabaseInstance(
				ConstantsHelper.get(ConstantsProperties.DATABASE_URL_PROPERTY, String.class),
				ConstantsHelper.get(ConstantsProperties.DATABASE_USERNAME_PROPERTY, String.class),
				ConstantsHelper.get(ConstantsProperties.DATABASE_PASSWORD_PROPERTY, String.class));
		BandaiSpyHelper.setDatabase(instance);
		BandaiSpyHelper.fillDatabaseIfEmpty();
		return instance;
	}
}
