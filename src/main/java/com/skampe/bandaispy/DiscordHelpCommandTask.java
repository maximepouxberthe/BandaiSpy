package com.skampe.bandaispy;

import java.util.List;

import com.skampe.utils.structures.DiscordCommandTask;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DiscordHelpCommandTask extends DiscordCommandTask {

	public DiscordHelpCommandTask(final MessageReceivedEvent event, final List<String> args) {
		super(event, args);
	}

	@Override
	public void run() {
		BandaiSpyHelper.sendHelp(event.getChannel().getId());
	}

}
