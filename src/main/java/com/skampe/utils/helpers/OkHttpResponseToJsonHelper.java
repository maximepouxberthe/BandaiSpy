package com.skampe.utils.helpers;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import okhttp3.Response;

public class OkHttpResponseToJsonHelper {

	private static final Logger LOGGER = LogManager.getLogger(OkHttpResponseToJsonHelper.class);

	private OkHttpResponseToJsonHelper() {
		// Hide public constructor
	}

	public static JsonNode getJsonNodeFromImgurAPIResponse(final Response response) {
		return getJsonNodeFromOkHttpResponse(response);
	}

	private static JsonNode getJsonNodeFromOkHttpResponse(final Response response) {

		final ObjectMapper mapper = new ObjectMapper();
		String responseStr;
		try {
			responseStr = response.body().string();
			response.body().close();
			try {
				return mapper.readTree(responseStr);
			} catch (final IOException e) {
				LOGGER.error(String.format("Failed to read json response: %s", responseStr));
				LOGGER.error(e);
				LOGGER.debug(response);
			}
		} catch (final IOException e1) {
			LOGGER.error("Failed to read json response");
			LOGGER.error(e1);
		}

		return null;
	}

}
