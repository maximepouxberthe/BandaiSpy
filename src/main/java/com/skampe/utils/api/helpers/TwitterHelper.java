package com.skampe.utils.api.helpers;

import java.io.ByteArrayInputStream;
import java.io.File;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterHelper {
	public static Twitter buildTwitterInstance(final String consumerKey, final String consumerSecret,
			final String accessToken, final String accessTokenSecret) {
		final ConfigurationBuilder twitterConfigBuilder = new ConfigurationBuilder();
		twitterConfigBuilder.setOAuthConsumerKey(consumerKey);
		twitterConfigBuilder.setOAuthConsumerSecret(consumerSecret);
		twitterConfigBuilder.setOAuthAccessToken(accessToken);
		twitterConfigBuilder.setOAuthAccessTokenSecret(accessTokenSecret);

		return new TwitterFactory(twitterConfigBuilder.build()).getInstance();
	}

	public static void sendTweet(final Twitter twitterInstance, final String message) throws TwitterException {
		final StatusUpdate status = new StatusUpdate(message);
		twitterInstance.updateStatus(status);
	}

	public static void sendTweet(final Twitter twitterInstance, final String message, final File file)
			throws TwitterException {
		final StatusUpdate status = new StatusUpdate(message);
		status.setMedia(file);
		twitterInstance.updateStatus(status);
	}

	public static void sendTweet(final Twitter twitterInstance, final String message, final String fileName,
			final byte[] file) throws TwitterException {
		final StatusUpdate status = new StatusUpdate(message);
		status.setMedia(fileName, new ByteArrayInputStream(file));
		twitterInstance.updateStatus(status);
	}

	private TwitterHelper() {
		// Hide public constructor
	}
}
