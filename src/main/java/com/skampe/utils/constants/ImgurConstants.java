package com.skampe.utils.constants;

public class ImgurConstants {

	private ImgurConstants() {
		// Hide public constructor
	}

	public static final String ALBUM_ENDPOINT = "https://api.imgur.com/3/album";
	public static final String IMAGE_ENDPOINT = "https://api.imgur.com/3/image";
	public static final String UPLOAD_ENDPOINT = "https://api.imgur.com/3/upload";

	public static final String IMAGE_FORMDATA_PART = "image";
	public static final String ALBUM_FORMDATA_PART = "album";
	public static final String TITLE_FORMDATA_PART = "title";

	public static final String CLIENT_ID_AUTHORIZATION_HEADER_VALUE = "Client-ID";

	/*
	 * Json response keys
	 */

	// Generic keys
	public static final String JSON_RESPONSE_SUCCESS_KEY = "success";
	public static final String JSON_RESPONSE_STATUS_KEY = "status";
	public static final String JSON_RESPONSE_DATA_KEY = "data";
	public static final String JSON_RESPONSE_METHOD_KEY = "method";
	public static final String JSON_RESPONSE_REQUEST_KEY = "request";
	public static final String JSON_RESPONSE_TYPE_KEY = "type";

	// Error keys
	public static final String JSON_RESPONSE_ERROR_KEY = "error";
	public static final String JSON_RESPONSE_CODE_KEY = "code";
	public static final String JSON_RESPONSE_MESSAGE_KEY = "message";
	public static final String JSON_RESPONSE_EXCEPTION_KEY = "exception";

	// Success keys
	// Generic success keys
	public static final String JSON_RESPONSE_ID_KEY = "id";
	public static final String JSON_RESPONSE_NAME_KEY = "name";
	public static final String JSON_RESPONSE_DESCRIPTION_KEY = "description";
	// Common to uploaded to Imgur and uploaded to Reddit imae keys (in
	// "https://api.imgur.com/3/image" or
	// https://api.imgur.com/3/gallery/r/ responses)
	public static final String JSON_RESPONSE_TITLE_KEY = "title";
	public static final String JSON_RESPONSE_DATETIME_KEY = "datetime";
	public static final String JSON_RESPONSE_ANIMATED_KEY = "animated";
	public static final String JSON_RESPONSE_WIDTH_KEY = "width";
	public static final String JSON_RESPONSE_HEIGHT_KEY = "height";
	public static final String JSON_RESPONSE_SIZE_KEY = "size";
	public static final String JSON_RESPONSE_VIEWS_KEY = "views";
	public static final String JSON_RESPONSE_BANDWIDTH_KEY = "bandwidth";
	public static final String JSON_RESPONSE_VOTE_KEY = "vote";
	public static final String JSON_RESPONSE_FAVORITE_KEY = "favorite";
	public static final String JSON_RESPONSE_NSFW_KEY = "nsfw";
	public static final String JSON_RESPONSE_SECTION_KEY = "section";
	public static final String JSON_RESPONSE_ACCOUNT_URL_KEY = "account_url";
	public static final String JSON_RESPONSE_ACCOUNT_ID_KEY = "account_id";
	public static final String JSON_RESPONSE_IS_AD_KEY = "is_ad";
	public static final String JSON_RESPONSE_IN_MOST_VIRAL_KEY = "in_most_viral";
	public static final String JSON_RESPONSE_TAGS_KEY = "tags";
	public static final String JSON_RESPONSE_IN_GALLERY_KEY = "in_gallery";
	public static final String JSON_RESPONSE_LINK_KEY = "link";
	// Specific uploaded to Imgur image keys (in "https://api.imgur.com/3/image"
	// responses)
	public static final String JSON_RESPONSE_HAS_SOUND_KEY = "has_sound";
	public static final String JSON_RESPONSE_AD_TYPE_KEY = "ad_type";
	public static final String JSON_RESPONSE_AD_URL_KEY = "ad_url";
	public static final String JSON_RESPONSE_EDITED_KEY = "edited";
	public static final String JSON_RESPONSE_DELETEHASH_KEY = "deletehash";
	// Specific uploaded to Reddit image keys (in
	// https://api.imgur.com/3/gallery/r/ responses)
	public static final String JSON_RESPONSE_COMMENT_COUNT_KEY = "comment_count";
	public static final String JSON_RESPONSE_UPS_KEY = "ups";
	public static final String JSON_RESPONSE_DOWNS_KEY = "downs";
	public static final String JSON_RESPONSE_POINTS_KEY = "points";
	public static final String JSON_RESPONSE_SCORE_KEY = "score";
	// Specific https://api.imgur.com/3/account/ keys
	public static final String JSON_RESPONSE_URL_KEY = "url";
	public static final String JSON_RESPONSE_BIO_KEY = "bio";
	public static final String JSON_RESPONSE_AVATAR_KEY = "avatar";
	public static final String JSON_RESPONSE_REPUTATION_KEY = "reputation";
	public static final String JSON_RESPONSE_REPUTATION_NAME_KEY = "reputation_name";
	public static final String JSON_RESPONSE_CREATED_KEY = "created";
	public static final String JSON_RESPONSE_PRO_EXPIRATION_KEY = "pro_expiration";
	public static final String JSON_RESPONSE_USER_FOLLOW_KEY = "user_follow";
	// Specific https://api.imgur.com/3/tags keys
	public static final String JSON_RESPONSE_DISPLAY_NAME_KEY = "display_name";
	public static final String JSON_RESPONSE_FOLLOWERS_KEY = "followers";
	public static final String JSON_RESPONSE_TOTAL_ITEMS_KEY = "total_items";
	public static final String JSON_RESPONSE_BACKGROUND_HASH_KEY = "background_hash";
	// Specific tag_info
	public static final String JSON_RESPONSE_FOLLOWING_KEY = "following";
	public static final String JSON_RESPONSE_IS_PROMOTED_KEY = "is_promoted";
	public static final String JSON_RESPONSE_LOGO_HASH_KEY = "logo_hash";
	public static final String JSON_RESPONSE_LOGO_DESTINATION_URL_KEY = "logo_destination_url";
	public static final String JSON_RESPONSE_LOGO_DESCRIPTION_ANNOTATIONS_KEY = "description_annotations";

	public static final String JSON_RESPONSE_ALBUMHASH_KEY = "albumHash";
}
