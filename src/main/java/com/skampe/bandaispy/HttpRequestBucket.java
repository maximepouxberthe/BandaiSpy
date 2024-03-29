package com.skampe.bandaispy;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.SSLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.skampe.utils.constants.HttpStatusConstants;
import com.skampe.utils.constants.OkHttpConstants;
import com.skampe.utils.helpers.FileHelper;
import com.skampe.utils.helpers.JavaNetHelper;
import com.skampe.utils.helpers.OkHttpHelper;
import com.skampe.utils.work.Bucket;

import okhttp3.Response;

public class HttpRequestBucket extends Bucket {

	private static final Logger LOGGER = LogManager.getLogger(HttpRequestBucket.class);

	List<String> urls;

	HttpRequestsWorker workerParent;

	public HttpRequestBucket(final List<String> urls, final HttpRequestsWorker workerParent) {
		this.urls = urls;
		this.workerParent = workerParent;
	}

	@Override
	public void run() {

		LOGGER.debug(String.format("Starting the work on %s bucket with %s urls", Thread.currentThread().getName(),
				urls.size()));
		final Iterator<String> iterator = urls.iterator();
		while (iterator.hasNext() && !workerParent.stopBuckets()) {
			final String url = iterator.next();
			try {
				testUrl(url, 0);
			} catch (final IOException e) {
				LOGGER.error(String.format("Failed to test url %s", url), e);
			}
		}
		if (workerParent.stopBuckets()) {
			LOGGER.debug(String.format("End of the work on %s bucket. The worker parent stopped the bucket",
					Thread.currentThread().getName()));
		} else {

			LOGGER.debug(String.format("End of the work on %s bucket. %s urls requested",
					Thread.currentThread().getName(), urls.size()));
		}
	}

	private void testUrl(final String url, final int tries) throws IOException {
		try {
			if (JavaNetHelper.getStatus(url) == HttpStatusConstants.SC_200_OK) {
				final Response response = OkHttpHelper.executeRequest(
						OkHttpHelper.createRequest().withUrl(url).withMethod(OkHttpConstants.METHOD_GET).build());
				if (OkHttpHelper.getHttpCodeFromResponse(response) == HttpStatusConstants.SC_200_OK) {
					final byte[] data = OkHttpHelper.getBodyFromResponseAsBytes(response);
					if (FileHelper.isValidPNG(data)) {
						LOGGER.info(String.format("Found an icon at %s url on %s bucket", url,
								Thread.currentThread().getName()));
						BandaiSpyHelper.setNewIconToDo(url);
						workerParent.setStopBuckets(true);
					}
				}
				response.close();
			}
		} catch (final SSLException | SocketException | InterruptedIOException e) {
			if (tries < 5) {
				LOGGER.info(String.format("Failed to test url %s, silently retrying", url));
				testUrl(url, tries + 1);
			} else {
				LOGGER.error(String.format("Failed to test url %s", url), e);
			}
		}
	}

}
