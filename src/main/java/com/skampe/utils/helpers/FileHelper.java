package com.skampe.utils.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;

import com.skampe.utils.constants.FileConstants;

public class FileHelper {

	private FileHelper() {
		// Hide public constructor
	}

	public static boolean isValidPNG(final byte[] bytes) throws IOException {
		return isValidFormat(bytes, FileConstants.MIME_TYPE_PNG);
	}

	private static boolean isValidFormat(final byte[] bytes, final String mimeType) throws IOException {
		return mimeType.equals(URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(bytes)));
	}
}
