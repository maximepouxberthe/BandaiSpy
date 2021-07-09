package com.skampe.utils.work;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Worker {

	private static final Logger LOGGER = LogManager.getLogger(Worker.class);

	protected ThreadPoolExecutor executor = null;

	public void work() {
		initExecutor();
		scheduleWork();
		executor.shutdown();
	}

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

	public void awaitTerminationOrForceStop(final long timeoutMillis) throws InterruptedException {
		if (!executor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS)) {
			LOGGER.warn("Force shutdown");
			executor.shutdownNow();
		}
	}
}
