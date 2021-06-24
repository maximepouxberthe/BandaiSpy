package com.skampe.utils.helpers;

import java.io.IOException;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

public class JacksonJsonHelper {

	static ObjectMapper mapper = new ObjectMapper();

	private JacksonJsonHelper() {
		// Hide public constructor
	}

	public static JsonNode getJsonNodeFromString(final String string) throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.readTree(string);
	}

	public static int getInt(final JsonNode node, final String key, final int returnValueIfError) {
		final JsonNode value = node.get(key);
		if (value != null && value.isNumber()) {
			return value.getIntValue();
		} else {
			return returnValueIfError;
		}
	}

	public static JsonNode getJsonNode(final JsonNode node, final String key) {
		final JsonNode value = node.get(key);
		if (value != null && value.isObject()) {
			return value;
		} else {
			return mapper.createObjectNode();
		}
	}

	public static String getString(final JsonNode node, final String key) {
		final JsonNode value = node.get(key);
		if (value != null && value.isTextual()) {
			return value.getTextValue();
		} else {
			return "";
		}
	}
}
