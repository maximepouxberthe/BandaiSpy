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
}
