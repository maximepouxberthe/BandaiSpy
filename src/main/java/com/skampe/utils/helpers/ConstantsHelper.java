package com.skampe.utils.helpers;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.javatuples.KeyValue;

import com.skampe.utils.exceptions.NotImplementedException;

public class ConstantsHelper {

	private static final Logger LOGGER = LogManager.getLogger(ConstantsHelper.class);

	private static final Map<String, String> constantsMap = new HashMap<>();

	private ConstantsHelper() {
		// Hide public constructor
	}

	private static void set(final String key, final String value) {
		constantsMap.put(key, value);
	}

	private static String get(final String key) {
		return constantsMap.get(key);
	}

	private static boolean setValue(final Map<?, ?> map, final String property, final boolean required) {
		final Object value = map.get(property);
		if (value instanceof String && !StringUtils.isBlank((String) value)) {
			LOGGER.debug(String.format("Setting '%s' property with '%s' value", property, value));
			set(property, (String) value);
			return true;
		} else {
			if (required) {
				LOGGER.error(String.format("Required property '%s' not set", property));
				return false;
			} else {
				LOGGER.warn(String.format(
						"Property '%s' not set, setting it to null since that property is not required", property));
				set(property, null);
				return true;
			}
		}
	}

	public static boolean initConstants(final List<KeyValue<String, Boolean>> properties, final String confPath)
			throws IOException {
		final ObjectMapper mapper = new ObjectMapper();
		final Map<?, ?> map = mapper.readValue(Paths.get(confPath).toFile(), Map.class);

		boolean areAllRequiredPropertiesDefined = true;

		for (final KeyValue<String, Boolean> property : properties) {

			LOGGER.debug(String.format("Reading %s '%s' property from conf file %s",
					Boolean.TRUE.equals(property.getValue()) ? "mandatory" : "", property.getKey(), confPath));
			areAllRequiredPropertiesDefined = areAllRequiredPropertiesDefined
					&& setValue(map, property.getKey(), property.getValue());
		}
		return areAllRequiredPropertiesDefined;
	}

	@SuppressWarnings("unchecked")
	public static <T> T get(final String key, final Class<T> returnType) {
		final String propertyValue = get(key);
		if (returnType.equals(String.class)) {
			return (T) propertyValue;
		} else if (returnType.equals(Integer.class)) {
			return (T) Integer.valueOf(propertyValue);
		} else if (returnType.equals(Boolean.class)) {
			return (T) Boolean.valueOf(propertyValue);
		} else {
			throw new NotImplementedException(
					String.format("%s type is not implemented right now.", returnType.getSimpleName()));
		}
	}

	public static String getListAsOrRegex(final List<String> list) {
		return list.toString().replace("[", "").replace("]", "").replace(", ", "|");
	}

}
