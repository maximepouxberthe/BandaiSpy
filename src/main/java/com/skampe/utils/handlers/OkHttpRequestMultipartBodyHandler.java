package com.skampe.utils.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.javatuples.Quartet;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Builder;
import okhttp3.RequestBody;

public class OkHttpRequestMultipartBodyHandler extends OkHttpRequestBodyHandler {

	private MediaType mediaType;
	private Map<String, String> formDataParts;

	private List<Quartet<String, String, MediaType, byte[]>> formDataMediaParts = new ArrayList<>();

	public final OkHttpRequestMultipartBodyHandler withFormDataParts(final Map<String, String> formDataParts) {
		this.formDataParts = formDataParts;
		return this;
	}

	public final OkHttpRequestMultipartBodyHandler withFormDataMediaParts(
			final List<Quartet<String, String, MediaType, byte[]>> formDataMediaParts) {
		this.formDataMediaParts = formDataMediaParts;
		return this;
	}

	public final OkHttpRequestMultipartBodyHandler withMultipartBodyMediaType() {
		this.mediaType = MultipartBody.FORM;
		return this;
	}

	@Override
	public RequestBody build() {
		final Builder builder = new MultipartBody.Builder().setType(mediaType);
		for (final Entry<String, String> formDataPart : formDataParts.entrySet()) {
			builder.addFormDataPart(formDataPart.getKey(), formDataPart.getValue());
		}
		for (final Quartet<String, String, MediaType, byte[]> formDataMediaPart : formDataMediaParts) {
			builder.addFormDataPart(formDataMediaPart.getValue0(), formDataMediaPart.getValue1(),
					RequestBody.create(formDataMediaPart.getValue2(), formDataMediaPart.getValue3()));

		}
		return builder.build();
	}

}
