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
import com.skampe.utils.work.Work;

import okhttp3.Response;

public class HttpRequestBucket implements Work {

	private static final Logger LOGGER = LogManager.getLogger(HttpRequestBucket.class);

	List<String> urls;

	HttpRequestsWorker workerParent;

	public HttpRequestBucket(final List<String> urls, final HttpRequestsWorker workerParent) {
		this.urls = urls;
		this.workerParent = workerParent;
	}

	@Override
	public void run() {
		/*
		 * LOGGER.debug(String.format("Starting the work on %s bucket with %s urls",
		 * Thread.currentThread().getName(), urls.size()));
		 */
		final Iterator<String> iterator = urls.iterator();
		while (iterator.hasNext() && !workerParent.stopBuckets()) {
			final String url = iterator.next();
			try {
				testUrl(url);
			} catch (final InterruptedIOException ie) {
				LOGGER.warn(String.format("Bucket %s has been interrupted", Thread.currentThread().getName()), ie);
				// End this thread
				return;
			} catch (final SSLException | SocketException e) {
				// might randomly happen, retry
				try {
					LOGGER.warn(String.format("Failed to test url %s, silently retrying", url), e);
					testUrl(url);
				} catch (final IOException e1) {
					LOGGER.error(String.format("Failed to test url %s", url), e1);
				}
			} catch (final IOException e) {
				LOGGER.error(String.format("Failed to test url %s", url), e);
			}
		}
		if (workerParent.stopBuckets()) {
			/*
			 * LOGGER.debug(String.
			 * format("End of the work on %s bucket. The worker parent stopped the bucket",
			 * Thread.currentThread().getName()));
			 */
		} else {
			/*
			 * LOGGER.debug(String.format("End of the work on %s bucket. %s urls requested",
			 * Thread.currentThread().getName(), urls.size()));
			 */
		}
	}

	private void testUrl(final String url) throws IOException {
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
	}

}
