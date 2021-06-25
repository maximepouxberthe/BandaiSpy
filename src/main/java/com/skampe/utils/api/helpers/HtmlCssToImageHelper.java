package com.skampe.utils.api.helpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.JsonNode;

import com.skampe.utils.constants.OkHttpConstants;
import com.skampe.utils.helpers.JacksonJsonHelper;
import com.skampe.utils.helpers.OkHttpHelper;

import okhttp3.Credentials;

public class HtmlCssToImageHelper {

	private static final Logger LOGGER = LogManager.getLogger(HtmlCssToImageHelper.class);

	public static final String buildImage(final String html, final String css) throws IOException {
		final Map<String, String> parameters = new HashMap<>();
		parameters.put("html", html);
		parameters.put("css", css);
		parameters.put("ms_delay", "5000");
		parameters.put("device_scale", "1");

		final String credential = Credentials.basic("c30175e4-8133-4c1a-a16b-efc357138bc7",
				"07df92a5-022e-46ae-9f65-db0b1a7fc6cc");
		final Map<String, String> headers = new HashMap<>();
		headers.put(OkHttpConstants.AUTHORIZATION_HEADER, credential);

		LOGGER.debug("Calling HtmlCssToImage API");
		final JsonNode response = JacksonJsonHelper
				.getJsonNodeFromString(OkHttpHelper.getBodyFromResponse(OkHttpHelper.executeRequest(OkHttpHelper
						.createRequest().withUrl("https://hcti.io/v1/image").withMethod(OkHttpConstants.METHOD_POST)
						.withRequestBody(OkHttpHelper.createRequestFormBody().withParameters(parameters).build())
						.withHeaders(headers).build())));
		LOGGER.debug("Got an answer from HtmlCssToImage API");
		if (response.has("url")) {
			LOGGER.debug("The call to HtmlCssToImage API was succesfull");
			return response.get("url").getTextValue();
		} else {
			LOGGER.warn(String.format("HtmlCssToImage conversion failed: %s %s: %s",
					response.get("statusCode").getIntValue(), response.get("error").getTextValue(),
					response.get("message").getTextValue()));
			return null;
		}
	}

	private HtmlCssToImageHelper() {
		// Hide public constructor
	}
}
