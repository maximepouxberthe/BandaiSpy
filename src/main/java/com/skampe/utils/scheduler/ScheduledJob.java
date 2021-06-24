package com.skampe.utils.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScheduledJob implements Job {

	private final String name;
	private final JobRunnable task;

	public ScheduledJob(final String name, final JobRunnable task) {
		this.name = name;
		this.task = task;
	}

	@Override
	public void execute(final JobExecutionContext context) throws JobExecutionException {
		task.setContext(context);
		task.run();
	}

	public String getName() {
		return name;
	}

}
