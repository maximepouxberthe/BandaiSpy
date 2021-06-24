package com.skampe.utils.helpers;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.skampe.utils.handlers.OkHttpRequestFormBodyHandler;
import com.skampe.utils.handlers.OkHttpRequestHandler;
import com.skampe.utils.handlers.OkHttpRequestMultipartBodyHandler;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpHelper {

	private static final Logger LOGGER = LogManager.getLogger(OkHttpHelper.class);

	private OkHttpHelper() {
		// Hide public constructor
	}

	public static Response executeRequest(final Request request) throws IOException {
		return executeRequestWithRetries(request, 5);

	}

	private static Response executeRequestWithRetries(final Request request, final int retries) throws IOException {

		final OkHttpClient client = new OkHttpClient();
		try {
			return client.newCall(request).execute();
		} catch (final SocketTimeoutException e) {
			if (retries > 0) {
				LOGGER.warn(String.format("Failed to execute request %s because of timeout. Will retry %s times",
						request.toString(), retries - 1));
				return executeRequestWithRetries(request, retries - 1);
			} else {
				throw e;
			}
		}
	}

	public static String getBodyFromResponse(final Response response) throws IOException {
		final String bodyResp = response.body().string();
		response.body().close();
		return bodyResp;
	}

	public static int getHttpCodeFromResponse(final Response response) {
		return response.code();
	}

	public static OkHttpRequestHandler createRequest() {
		return new OkHttpRequestHandler();
	}

	public static OkHttpRequestMultipartBodyHandler createRequestMultipartBody() {
		return new OkHttpRequestMultipartBodyHandler();
	}

	public static OkHttpRequestFormBodyHandler createRequestFormBody() {
		return new OkHttpRequestFormBodyHandler();
	}

	public static byte[] getBodyFromResponseAsBytes(final Response response) throws IOException {
		final byte[] bodyResp = response.body().bytes();
		response.body().close();
		return bodyResp;
	}
}
