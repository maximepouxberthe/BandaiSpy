package com.skampe.bandaispy;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.skampe.utils.structures.DiscordCommandTask;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DiscordRegisterChannelCommandTask extends DiscordCommandTask {

	private static final Logger LOGGER = LogManager.getLogger(DiscordRegisterChannelCommandTask.class);

	private static String version = "";
	private static String type = "";

	public DiscordRegisterChannelCommandTask(final MessageReceivedEvent event, final List<String> args) {
		super(event, args);
	}

	@Override
	public void run() {
		if (checkAndSetParameters(args)) {
			BandaiSpyHelper.registerChannel(event.getChannel().getId(), version, type);
		}
	}

	private static boolean checkAndSetParameters(final List<String> args) {
		final String arg1 = args.get(0);
		final String arg2 = args.get(1);

		if (!arg1.toLowerCase().matches(BandaiSpyConstants.SERVERS_REGEX)) {
			LOGGER.info("Invalid parameter version");
			return false;
		}

		if (!arg2.toLowerCase().matches(BandaiSpyConstants.REGISTERCHANNEL_COMMAND_ALLOWED_TYPES_REGEX)) {
			LOGGER.info("Invalid parameter type");
			return false;
		}

		version = arg1;
		type = arg2;

		return true;
	}
}
