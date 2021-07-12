package com.skampe.utils.bots;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.skampe.utils.helpers.InstanceHelper;
import com.skampe.utils.helpers.SchedulerHelper;
import com.skampe.utils.helpers.discord.DiscordBotHelper;
import com.skampe.utils.helpers.discord.DiscordLogHelper;
import com.skampe.utils.structures.DiscordCommand;
import com.skampe.utils.structures.DiscordCommandTask;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordBot extends ListenerAdapter {

	private static final Logger LOGGER = LogManager.getLogger(DiscordBot.class);

	private boolean isReady = false;
	private final List<DiscordCommand> commands;

	private final ThreadPoolExecutor commandsExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L,
			TimeUnit.SECONDS, new SynchronousQueue<>());
	private final ThreadPoolExecutor onMessageReceivedExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L,
			TimeUnit.SECONDS, new SynchronousQueue<>());

	private final List<Future<?>> commandsTasksResults = new ArrayList<>();
	private final List<Future<?>> onMessageReceivedTasksResults = new ArrayList<>();

	public DiscordBot(final List<DiscordCommand> commands) {
		this.commands = commands;
		try {
			scheduleExceptionsCatcher("0 * * ? * * *");
		} catch (final SchedulerException e) {
			LOGGER.error("Failed to schedule exceptions catcher", e);
		}
	}

	public DiscordBot(final List<DiscordCommand> commands, final String cronExpressionForExecutorsCleaning) {
		this.commands = commands;
		try {
			scheduleExceptionsCatcher(cronExpressionForExecutorsCleaning);
		} catch (final SchedulerException e) {
			LOGGER.error("Failed to schedule exceptions catcher", e);
		}
	}

	private void scheduleExceptionsCatcher(final String cronExpressionForExecutorsCleaning) throws SchedulerException {
		SchedulerHelper.schedule(DiscordBotExceptionsCatcherJob.class, cronExpressionForExecutorsCleaning);
	}

	@Override
	public void onReady(final ReadyEvent e) {
		isReady = true;
	}

	public boolean isReady() {
		return isReady;
	}

	@Override
	public void onMessageReceived(final MessageReceivedEvent event) {
		final Runnable onMessageReceivedTask = () -> onMessageReceivedEventTask(event);
		onMessageReceivedTasksResults.add(onMessageReceivedExecutor.submit(onMessageReceivedTask));
	}

	private void onMessageReceivedEventTask(final MessageReceivedEvent event) {
		if (!event.getAuthor().isBot()) { // avoid loops by replying to other bots
			final String message = event.getMessage().getContentRaw();
			for (final DiscordCommand command : commands) {
				if (message.startsWith(command.getCommand())) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(String.format("Event %s matches a command: %s",
								DiscordLogHelper.getEventDescription(event),
								DiscordLogHelper.getCommandDescription(command)));
					}
					final List<String> args = DiscordBotHelper.getArgs(event, command);
					if (DiscordBotHelper.isCommandAllowed(event, command)
							&& command.getNbExpectedArguments() == args.size()) {
						launchCommandTask(command, event, args);
					}
				}
			}
		}
	}

	private void launchCommandTask(final DiscordCommand command, final MessageReceivedEvent event,
			final List<String> args) {
		final DiscordCommandTask commandTask = (DiscordCommandTask) InstanceHelper.getInstanceOfParentClassWithParams(
				command.getCommandTaskId(), DiscordCommandTask.class, DiscordCommandTask.getConstructor(),
				new Object[] { event, args });
		if (commandTask != null) {
			commandsTasksResults.add(commandsExecutor.submit(commandTask));
		}
	}

	public void sendMessage(final TextChannel channel, final byte[] attachment, final String filename) {
		if (channel.canTalk()) {
			LOGGER.debug(String.format("Sending attachment to %s channel", channel.getId()));
			channel.sendFile(attachment, filename).queue();
		} else {
			LOGGER.debug(
					String.format("Tried to send a message to %s channel but didn't have the rights", channel.getId()));
		}
	}

	public void sendMessage(final TextChannel channel, final String message) {
		if (channel.canTalk()) {
			LOGGER.debug(String.format("Sending message to %s channel: %s", channel.getId(), message));
			channel.sendMessage(message).queue();
		} else {
			LOGGER.debug(
					String.format("Tried to send a message to %s channel but didn't have the rights", channel.getId()));
		}

	}

	public void react(final Message message, final String emoteUnicode) {
		message.addReaction(emoteUnicode).queue();
	}

	public class DiscordBotExceptionsCatcherJob implements Job {

		@Override
		public void execute(final JobExecutionContext context) throws JobExecutionException {
			for (final Future<?> result : commandsTasksResults) {
				try {
					result.get();
				} catch (InterruptedException | ExecutionException e) {
					LOGGER.error(e);
				}
			}
			for (final Future<?> result : onMessageReceivedTasksResults) {
				try {
					result.get();
				} catch (InterruptedException | ExecutionException e) {
					LOGGER.error(e);
				}
			}
		}
	}
}
