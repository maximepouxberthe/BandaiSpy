package com.skampe.bandaispy;

import java.util.Arrays;
import java.util.List;

import com.skampe.utils.helpers.ConstantsHelper;

public class BandaiSpyConstants {

	private BandaiSpyConstants() {
		// Hide public constructor
	}

	public static final String GLB_SERVER = "glb";
	public static final String JPN_SERVER = "jpn";

	public static final String GLB_URL = "https://cdn.gb.onepiece-tc.jp/fr/images/characters/character_%s_t.png";
	public static final String JPN_URL = "https://cdn.onepiece-tc.jp/images/characters/character_%s_t.png";

	public static final List<String> SERVERS_LIST = Arrays.asList(GLB_SERVER, JPN_SERVER);
	public static final String SERVERS_REGEX = ConstantsHelper.getListAsOrRegex(SERVERS_LIST);

	public static final String OVERVIEWS = "overviews";
	public static final String ICONS = "icons";
	public static final String IMGUR_ALBUMS = "imgurAlbums";

	private static final List<String> REGISTERCHANNEL_COMMAND_ALLOWED_TYPES_LIST = Arrays.asList(OVERVIEWS, ICONS,
			IMGUR_ALBUMS);
	public static final String REGISTERCHANNEL_COMMAND_ALLOWED_TYPES_REGEX = ConstantsHelper
			.getListAsOrRegex(REGISTERCHANNEL_COMMAND_ALLOWED_TYPES_LIST);

	public static final String PROPERTIES_FILE = "conf.json";

	public static final String OVERVIEW_CSS = "body { background-color: rgb(91,91,91); color: rgb(255,255,255); font-size:24;} "
			+ "div { padding-left:3px; padding-right:3px; padding-bottom:3px; width:576px; overflow:hidden;} "
			+ "p { margin:3px;} " + "img {width:112px; margin:1px; float:left; display:inline;}";

	public static final String HELP_MESSAGE = "**Commands:**\n`$bandaiSpy help` : Display this message\n\nTo run the following commands the user needs to have the \"Manage Server\" or \"Administrator\" permissions.\n`$bandaiSpy registerChannel glb overviews` : The channel where this command is used will receive the next overviews of the new icons of the GLB version: <https://bit.ly/36sSbvq>\n`$bandaiSpy registerChannel jpn overviews` : The channel where this command is used will receive the next overviews of the new icons of the JPN version: <https://bit.ly/2UDBE5a>\n`$bandaiSpy registerChannel glb imgurAlbums` : The channel where this command is used will receive the next auto-generated imgur albums (like this one: <https://imgur.com/a/b8w1dkC>) for the GLB version: <https://bit.ly/3ASPh0Y>\n`$bandaiSpy registerChannel jpn imgurAlbums` : The channel where this command is used will receive the next auto-generated imgur albums (like this one: <https://imgur.com/a/sqCCCiP>) for the JPN version: <https://bit.ly/3hB5Yqh>\n`$bandaiSpy registerChannel glb icons` : The channel where this command is used will receive the new icons for the GLB version: <https://bit.ly/3e5gljV>\n`$bandaiSpy registerChannel jpn icons` : The channel where this command is used will receive the new icons for the GLB version: <https://bit.ly/3kbzxQO>\n`$bandaiSpy unregisterChannel` : The channel where this command is used will no longer receive any message from this bot.\n\nContact Mask#3992 if you have any question or if you notice weird behaviors or bugs.";
	public static final String BOT_DESCRIPTION = "$bandaiSpy help";
}
