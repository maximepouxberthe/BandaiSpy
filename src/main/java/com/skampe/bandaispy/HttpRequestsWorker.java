package com.skampe.bandaispy;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.skampe.utils.work.FixedThreadPoolWorker;

public class HttpRequestsWorker extends FixedThreadPoolWorker {

	private static final Logger LOGGER = LogManager.getLogger(HttpRequestsWorker.class);
	List<String> urls;
	boolean stopBuckets = false;
	boolean allowStopBuckets;

	public HttpRequestsWorker(final int corePoolSize, final List<String> urls, final boolean allowStopBuckets) {
		super(corePoolSize);
		this.urls = urls;
		this.allowStopBuckets = allowStopBuckets;
		LOGGER.info("test1");
		this.setExecutorName("HttpRequestsWorker Executor");
		LOGGER.info("test2");
	}

	@Override
	protected void scheduleWork() {
		LOGGER.info("ici");
		int remaider = urls.size() % corePoolSize;
		final int number = urls.size() / corePoolSize;
		int offset = 0;
		for (int i = 0; i < corePoolSize; i++) {
			HttpRequestBucket bucket;
			if (remaider > 0) {
				bucket = new HttpRequestBucket(urls.subList(i * number + offset, (i + 1) * number + offset + 1), this);
				remaider--;
				offset++;
			} else {
				bucket = new HttpRequestBucket(urls.subList(i * number + offset, (i + 1) * number + offset), this);
			}
			bucket.setName(String.format("Bucket %s", i));
			this.scheduleBucket(bucket);
		}
	}

	public boolean stopBuckets() {
		return stopBuckets;
	}

	public void setStopBuckets(final boolean bool) {
		if (allowStopBuckets) {
			stopBuckets = bool;
		}
	}

	@Override
	protected Logger getLogger() {
		return LOGGER;
	}

}
