package com.skampe.utils.helpers.discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class DiscordHelper {

	private static JDA jda;

	private DiscordHelper() {
		// Hide public constructor
	}

	public static void setJDA(final JDA jdaParam) {
		jda = jdaParam;
	}

	public static User getUserFromId(final String id) {
		return jda.getUserById(id);
	}

	public static TextChannel getChannelFromId(final String id) {
		return jda.getTextChannelById(id);
	}

	public static Guild getServerFromId(final String id) {
		return jda.getGuildById(id);
	}

	public static List<User> getUsersFromIds(final List<String> ids) {
		final List<User> users = new ArrayList<>();
		for (final String id : ids) {
			users.add(getUserFromId(id));
		}
		return users;
	}

	public static List<TextChannel> getChannelsFromIds(final List<String> ids) {
		final List<TextChannel> channels = new ArrayList<>();
		for (final String id : ids) {
			channels.add(getChannelFromId(id));
		}
		return channels;
	}

	public static List<Guild> getServersFromIds(final List<String> ids) {
		final List<Guild> servers = new ArrayList<>();
		for (final String id : ids) {
			servers.add(getServerFromId(id));
		}
		return servers;
	}

	public static boolean hasAtLeastOnePermission(final Guild server, final User user,
			final List<Permission> permissions) {
		for (final Permission permission : permissions) {
			if (server.getMember(user).hasPermission(permission)) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasPermissions(final Guild server, final User user, final List<Permission> permissions) {
		for (final Permission permission : permissions) {
			if (!server.getMember(user).hasPermission(permission)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isAdmin(final Guild server, final User user) {
		return hasPermissions(server, user, Arrays.asList(Permission.ADMINISTRATOR));
	}

}
