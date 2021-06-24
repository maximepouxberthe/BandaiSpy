package com.skampe.utils.work;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class FixedThreadPoolWorker extends Worker {

	protected final int corePoolSize;

	public FixedThreadPoolWorker(final int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	@Override
	protected void initExecutor() {
		executor = new ThreadPoolExecutor(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>());
	}

}
