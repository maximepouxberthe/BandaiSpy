package com.skampe.utils.work;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;

public abstract class Worker {

	protected ThreadPoolExecutor executor = null;

	public void work() {
		initExecutor();
		scheduleWork();
		executor.shutdown();
	}

	protected abstract Logger getLogger();

	protected abstract void scheduleWork();

	protected abstract void initExecutor();

	protected void scheduleBucket(final Work bucket) {
		executor.execute(bucket);
	}

	public boolean isWorkDone() {
		return executor == null || executor.getActiveCount() == 0;
	}

	public boolean awaitTermination(final long timeoutMillis) throws InterruptedException {
		return executor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS);
	}

	public boolean awaitTerminationOrForceStop(final long timeoutMillis) throws InterruptedException {
		if (!executor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS)) {
			getLogger().warn("Force shutdown");
			getLogger().info(String.format("%s buckets were still active", executor.getPoolSize()));
			executor.shutdownNow();
			return false;
		}
		return true;
	}
}
