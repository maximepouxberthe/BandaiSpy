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
		final int subListsSize = urls.size() / corePoolSize;
		for (int i = 0; i < corePoolSize; i++) {
			this.scheduleBucket(new HttpRequestBucket(
					urls.subList(i * subListsSize, Math.min((i + 1) * subListsSize, urls.size())), this));
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
