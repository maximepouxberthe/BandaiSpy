package com.skampe.utils.api.helpers;

import it.grabz.grabzit.GrabzItClient;
import it.grabz.grabzit.GrabzItFile;
import it.grabz.grabzit.enums.ImageFormat;
import it.grabz.grabzit.parameters.ImageOptions;

public class GrabzItHelper {

	public static byte[] generatePNGForHtml(final String html) throws Exception {
		final GrabzItClient grabzIt = new GrabzItClient("MTQ0ZTU1NGQ5N2YwNGVjN2E1YzI3Mzg1YzM3NTE5YjE=",
				"P341VRM6Jx4DQj8OPz8/Pz8/Pz8/P1hRPz8/CT94Pz8=");

		final ImageOptions options = new ImageOptions();
		options.setFormat(ImageFormat.PNG);
		options.setBrowserWidth(588);
		options.setBrowserHeight(-1);
		options.setDelay(5000);

		grabzIt.HTMLToImage(html, options);

		final GrabzItFile result = getResultWithRetries(grabzIt, 5);

		return result.getBytes();
	}

	private static GrabzItFile getResultWithRetries(final GrabzItClient grabzIt, final int retries) {
		try {
			return grabzIt.SaveTo();
		} catch (final Exception e) {
			if (retries > 0) {
				System.out.println(retries);
				return getResultWithRetries(grabzIt, retries - 1);
			} else {
				return new GrabzItFile(new byte[0]);
			}
		}
	}

	private GrabzItHelper() {
		// Hide public constructor
	}
}
