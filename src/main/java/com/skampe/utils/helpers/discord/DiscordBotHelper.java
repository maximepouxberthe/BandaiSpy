package com.skampe.utils.helpers.discord;

import java.util.Arrays;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.skampe.utils.bots.DiscordBot;
import com.skampe.utils.structures.DiscordCommand;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DiscordBotHelper {

	private static final Logger LOGGER = LogManager.getLogger(DiscordBotHelper.class);

	private DiscordBotHelper() {
		// Hide public constructor
	}

	public static void buildBot(final DiscordBot bot, final String botToken) throws LoginException {
		final JDABuilder builder = JDABuilder.createDefault(botToken);

		builder.addEventListeners(bot);
		final JDA jda = builder.build();
		DiscordHelper.setJDA(jda);
	}

	public static void sendMessage(final DiscordBot bot, final String channelId, final byte[] attachment,
			final String filename) {
		final TextChannel channel = DiscordHelper.getChannelFromId(channelId);
		bot.sendMessage(channel, attachment, filename);
	}

	public static void sendMessage(final DiscordBot bot, final String channelId, final String message) {
		final TextChannel channel = DiscordHelper.getChannelFromId(channelId);
		bot.sendMessage(channel, message);
	}

	public static boolean isCommandAllowed(final MessageReceivedEvent event, final DiscordCommand command) {
		final User author = event.getAuthor();
		final Guild server = event.getGuild();
		final List<String> allowedUsers = command.getIdsOfUsersAllowed();
		final List<Permission> neededPermissions = command.getRequiredPermissions();
		if (!allowedUsers.isEmpty()) {
			if (allowedUsers.contains(author.getId())) {
				return true;
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(String.format("%s not allowed to perform %s because its exclusive to allowed users",
							DiscordLogHelper.getUserDescription(author),
							DiscordLogHelper.getCommandDescription(command)));
				}
				return false;
			}
		} else if (!neededPermissions.isEmpty()) {
			if (DiscordHelper.hasAtLeastOnePermission(server, author, neededPermissions)) {
				return true;
			} else {
				LOGGER.debug(String.format(
						"%s not allowed to perform %s because this user doesn't have required permissions",
						DiscordLogHelper.getUserDescription(author), DiscordLogHelper.getCommandDescription(command)));
				return false;
			}
		} else {
			return true;
		}
	}

	public static List<String> getArgs(final MessageReceivedEvent event, final DiscordCommand command) {
		final String message = event.getMessage().getContentRaw();
		final String args = message.replace(command.getCommand(), "").substring(1);
		return Arrays.asList(args.split(" "));
	}

}
