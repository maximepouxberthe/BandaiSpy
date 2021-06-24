package com.skampe.utils.helpers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class JavaNetHelper {

	public static int getStatus(final String urlStr) throws IOException {
		final URL url = new URL(urlStr);
		final HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		huc.setRequestMethod("HEAD");
		return huc.getResponseCode();
	}

	private JavaNetHelper() {
		// Hide public constructor
	}
}
