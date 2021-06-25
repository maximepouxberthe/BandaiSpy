package com.skampe.bandaispy;

import java.util.List;

import com.skampe.utils.work.FixedThreadPoolWorker;

public class HttpRequestsWorker extends FixedThreadPoolWorker {

	List<String> urls;
	boolean stopBuckets = false;
	boolean allowStopBuckets;

	public HttpRequestsWorker(final int corePoolSize, final List<String> urls, final boolean allowStopBuckets) {
		super(corePoolSize);
		this.urls = urls;
		this.allowStopBuckets = allowStopBuckets;
	}

	@Override
	protected void scheduleWork() {

		int remaider = urls.size() % corePoolSize;
		final int number = urls.size() / corePoolSize;
		int offset = 0;
		for (int i = 0; i < corePoolSize; i++) {
			if (remaider > 0) {
				this.scheduleBucket(
						new HttpRequestBucket(urls.subList(i * number + offset, (i + 1) * number + offset + 1), this));
				remaider--;
				offset++;
			} else {
				this.scheduleBucket(
						new HttpRequestBucket(urls.subList(i * number + offset, (i + 1) * number + offset), this));
			}
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

}
