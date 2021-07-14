package com.skampe.utils.work;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public abstract class FixedThreadPoolWorker extends Worker {

	protected final int corePoolSize;

	protected FixedThreadPoolWorker(final int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	@Override
	protected void initExecutor(final String workName) {
		executor = new WorkerThreadPoolExecutor(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<>());
		executor.setName(workName);
	}

}
