package com.skampe.bandaispy;

import java.util.List;

import com.skampe.utils.structures.DiscordCommandTask;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DiscordUnregisterChannelCommandTask extends DiscordCommandTask {

	public DiscordUnregisterChannelCommandTask(final MessageReceivedEvent event, final List<String> args) {
		super(event, args);
	}

	@Override
	public void run() {
		BandaiSpyHelper.unregisterChannel(event.getChannel().getId());
	}

}
