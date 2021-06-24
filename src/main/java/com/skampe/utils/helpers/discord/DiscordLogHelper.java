package com.skampe.utils.helpers.discord;

import java.util.ArrayList;
import java.util.List;

import com.skampe.utils.structures.DiscordCommand;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DiscordLogHelper {

	private DiscordLogHelper() {
		// Hide public constructor
	}

	public static String getEventDescription(final MessageReceivedEvent event) {
		final User user = event.getAuthor();
		final MessageChannel channel = event.getChannel();
		final Guild server = event.getGuild();
		return String.format("%s on  %s on  %s with message %s", getUserDescription(user),
				getChannelDescription(channel), getServerDescription(server), event.getMessage().getContentRaw());
	}

	public static String getServerDescription(final Guild server) {
		return String.format("server %s of id %s", server.getName(), server.getId());
	}

	public static String getChannelDescription(final MessageChannel channel) {
		return String.format("channel %s of id %s", channel.getName(), channel.getId());
	}

	public static String getUserDescription(final User user) {
		return String.format("user %s of id %s", user.getName(), user.getId());
	}

	public static String getCommandDescription(final DiscordCommand command) {
		return String.format(
				"command '%s' restricted to users %s or to users with %s permissions on the server the command was performed and expecting %s arguments",
				command.getCommand(),
				getUsersDescription(DiscordHelper.getUsersFromIds(command.getIdsOfUsersAllowed())),
				command.getRequiredPermissions(), command.getNbExpectedArguments());
	}

	public static List<String> getUsersDescription(final List<User> users) {
		final List<String> descriptions = new ArrayList<>();
		for (final User user : users) {
			descriptions.add(getUserDescription(user));
		}
		return descriptions;
	}
}
