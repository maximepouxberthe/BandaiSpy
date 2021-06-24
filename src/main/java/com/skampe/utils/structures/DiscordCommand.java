package com.skampe.utils.structures;

import java.util.List;

import net.dv8tion.jda.api.Permission;

public class DiscordCommand {

	private final String command;
	private final int nbExpectedArguments;
	private final String commandTaskId;
	private final List<String> idsOfUsersAllowed;
	private final List<Permission> requiredPermissions;

	public DiscordCommand(final String command, final int nbExpectedArguments, final String commandTaskId,
			final List<String> idsOfUsersAllowed, final List<Permission> requiredPermissions) {
		this.command = command;
		this.nbExpectedArguments = nbExpectedArguments;
		this.commandTaskId = commandTaskId;
		this.idsOfUsersAllowed = idsOfUsersAllowed;
		this.requiredPermissions = requiredPermissions;
	}

	public String getCommand() {
		return command;
	}

	public int getNbExpectedArguments() {
		return nbExpectedArguments;
	}

	public String getCommandTaskId() {
		return commandTaskId;
	}

	public List<String> getIdsOfUsersAllowed() {
		return idsOfUsersAllowed;
	}

	public List<Permission> getRequiredPermissions() {
		return requiredPermissions;
	}
}
