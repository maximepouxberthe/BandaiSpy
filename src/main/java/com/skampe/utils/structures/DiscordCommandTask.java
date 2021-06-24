package com.skampe.utils.structures;

import java.util.List;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class DiscordCommandTask implements Runnable {

	protected final MessageReceivedEvent event;
	protected final List<String> args;

	protected DiscordCommandTask(final MessageReceivedEvent event, final List<String> args) {
		this.event = event;
		this.args = args;
	}

	public static Class<?>[] getConstructor() {
		return new Class<?>[] { MessageReceivedEvent.class, List.class };
	}
}
