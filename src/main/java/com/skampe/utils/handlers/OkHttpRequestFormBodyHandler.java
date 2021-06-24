package com.skampe.utils.handlers;

import java.util.Map;
import java.util.Map.Entry;

import okhttp3.FormBody;
import okhttp3.FormBody.Builder;
import okhttp3.RequestBody;

public class OkHttpRequestFormBodyHandler extends OkHttpRequestBodyHandler {

	private Map<String, String> parameters;

	public final OkHttpRequestFormBodyHandler withParameters(final Map<String, String> parameters) {
		this.parameters = parameters;
		return this;
	}

	@Override
	public RequestBody build() {
		final Builder builder = new FormBody.Builder();

		for (final Entry<String, String> parameter : parameters.entrySet()) {
			builder.add(parameter.getKey(), parameter.getValue());
		}

		return builder.build();
	}

}
