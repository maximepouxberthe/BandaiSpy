package com.skampe.utils.scheduler;

import org.quartz.JobExecutionContext;

public abstract class JobRunnable implements Runnable {

	private JobExecutionContext context;

	public void setContext(final JobExecutionContext context) {
		this.context = context;
	}

	protected JobExecutionContext getContext() {
		return context;
	}

}
