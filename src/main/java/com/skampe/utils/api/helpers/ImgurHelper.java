package com.skampe.utils.api.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonNode;
import org.javatuples.Quartet;

import com.skampe.utils.constants.ImgurConstants;
import com.skampe.utils.constants.OkHttpConstants;
import com.skampe.utils.helpers.JacksonJsonHelper;
import com.skampe.utils.helpers.OkHttpHelper;
import com.skampe.utils.helpers.OkHttpResponseToJsonHelper;

import okhttp3.MediaType;

public class ImgurHelper {

	private ImgurHelper() {
		// Hide public constructor
	}

	private static String buildAuthorizationHeaderFromClientId(final String clientId) {
		return String.format("%s %s", ImgurConstants.CLIENT_ID_AUTHORIZATION_HEADER_VALUE, clientId);
	}

	public static JsonNode createAlbum(final String clientId, final String albumName) throws IOException {
		final Map<String, String> headers = new HashMap<>();
		headers.put(OkHttpConstants.AUTHORIZATION_HEADER, buildAuthorizationHeaderFromClientId(clientId));

		final Map<String, String> formDataParts = new HashMap<>();
		formDataParts.put(ImgurConstants.TITLE_FORMDATA_PART, albumName);

		return JacksonJsonHelper
				.getJsonNodeFromString(
						OkHttpHelper
								.getBodyFromResponse(OkHttpHelper.executeRequest(OkHttpHelper.createRequest()
										.withUrl(ImgurConstants.ALBUM_ENDPOINT).withMethod(OkHttpConstants.METHOD_POST)
										.withRequestBody(OkHttpHelper.createRequestMultipartBody()
												.withMultipartBodyMediaType().withFormDataParts(formDataParts).build())
										.withHeaders(headers).build())));
	}

	public static JsonNode addImageToAlbumFromUrl(final String clientId, final String albumHash, final String url)
			throws IOException {
		final Map<String, String> headers = new HashMap<>();
		headers.put(OkHttpConstants.AUTHORIZATION_HEADER, buildAuthorizationHeaderFromClientId(clientId));

		final Map<String, String> formDataParts = new HashMap<>();
		formDataParts.put(ImgurConstants.ALBUM_FORMDATA_PART, albumHash);
		formDataParts.put(ImgurConstants.IMAGE_FORMDATA_PART, url);

		return OkHttpResponseToJsonHelper
				.getJsonNodeFromImgurAPIResponse(
						OkHttpHelper
								.executeRequest(OkHttpHelper.createRequest().withUrl(ImgurConstants.IMAGE_ENDPOINT)
										.withMethod(OkHttpConstants.METHOD_POST)
										.withRequestBody(OkHttpHelper.createRequestMultipartBody()
												.withMultipartBodyMediaType().withFormDataParts(formDataParts).build())
										.withHeaders(headers).build()));
	}

	public static JsonNode addImageToAlbumFromData(final String clientId, final String albumHash,
			final String imageName, final MediaType mediaType, final byte[] imageData) throws IOException {
		final Map<String, String> headers = new HashMap<>();
		headers.put(OkHttpConstants.AUTHORIZATION_HEADER, buildAuthorizationHeaderFromClientId(clientId));

		final Map<String, String> formDataParts = new HashMap<>();
		formDataParts.put(ImgurConstants.ALBUM_FORMDATA_PART, albumHash);

		final List<Quartet<String, String, MediaType, byte[]>> formDataMediaParts = new ArrayList<>();
		formDataMediaParts.add(new Quartet<>(ImgurConstants.IMAGE_FORMDATA_PART, imageName, mediaType, imageData));

		return OkHttpResponseToJsonHelper.getJsonNodeFromImgurAPIResponse(OkHttpHelper.executeRequest(OkHttpHelper
				.createRequest().withUrl(ImgurConstants.IMAGE_ENDPOINT).withMethod(OkHttpConstants.METHOD_POST)
				.withRequestBody(OkHttpHelper.createRequestMultipartBody().withMultipartBodyMediaType()
						.withFormDataParts(formDataParts).withFormDataMediaParts(formDataMediaParts).build())
				.withHeaders(headers).build()));
	}

	public static JsonNode getAlbum(final String clientId, final String albumHash) throws IOException {
		final Map<String, String> headers = new HashMap<>();
		headers.put(OkHttpConstants.AUTHORIZATION_HEADER, buildAuthorizationHeaderFromClientId(clientId));

		return OkHttpResponseToJsonHelper.getJsonNodeFromImgurAPIResponse(OkHttpHelper.executeRequest(
				OkHttpHelper.createRequest().withUrl(String.format("%s/%s", ImgurConstants.ALBUM_ENDPOINT, albumHash))
						.withMethod(OkHttpConstants.METHOD_GET).withHeaders(headers).build()));
	}
}
