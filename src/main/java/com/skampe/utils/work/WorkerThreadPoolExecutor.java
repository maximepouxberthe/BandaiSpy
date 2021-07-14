package com.skampe.utils.work;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorkerThreadPoolExecutor extends ThreadPoolExecutor {

	private static final Logger LOGGER = LogManager.getLogger(WorkerThreadPoolExecutor.class);
	private String name;

	public WorkerThreadPoolExecutor(final int corePoolSize, final int maximumPoolSize, final long keepAliveTime,
			final TimeUnit unit, final BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	protected void beforeExecute(final Thread t, final Runnable r) {
		if (r instanceof Bucket) {
			LOGGER.trace(String.format("%s bucket on %s executor starting", ((Bucket) r).getName(), getName()));
		}
		super.beforeExecute(t, r);
	}

	@Override
	protected void afterExecute(final Runnable r, final Throwable t) {
		if (r instanceof Bucket) {
			LOGGER.trace(String.format("%s bucket on %s executor ended", ((Bucket) r).getName(), getName()));
		}
		super.afterExecute(r, t);
	}

	@Override
	public void execute(final Runnable r) {
		if (r instanceof Bucket) {
			LOGGER.trace(String.format("%s bucket on %s executor scheduled", ((Bucket) r).getName(), getName()));
		}
		super.execute(r);
	}
}
