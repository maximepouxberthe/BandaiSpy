package com.skampe.utils.helpers;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerHelper {

	private static Scheduler scheduler;

	private SchedulerHelper() {
		// Hide public constructor
	}

	public static void initScheduler() throws SchedulerException {
		final SchedulerFactory schedulerFactory = new StdSchedulerFactory();
		scheduler = schedulerFactory.getScheduler();
		scheduler.start();
	}

	public static void shutdownScheduler() throws SchedulerException {
		scheduler.shutdown();
	}

	public static void schedule(final Class<? extends Job> jobClass, final String cronExpression)
			throws SchedulerException {
		final JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(jobClass.getName(), "group1").build();
		final Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity(String.format("Trigger for %s", jobClass.getName()), "group1")
				.withSchedule(CronScheduleBuilder.cronSchedule(cronExpression)).build();
		scheduler.scheduleJob(jobDetail, trigger);
	}

	public static void schedule(final Runnable task, final int delayMillis) {
		final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.schedule(task, delayMillis, TimeUnit.MILLISECONDS);
	}
}
