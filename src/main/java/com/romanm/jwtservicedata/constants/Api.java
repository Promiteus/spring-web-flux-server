package com.romanm.jwtservicedata.constants;

public class Api {
    public static final String X_CONFIRMED_UID = "X-CONFIRMED-UID";

    public final static String API_PREFIX = "/api";
    public final static String PARAM_PAGE = "page";
    public final static String PARAM_PAGE_SIZE = "size";
    public final static String PARAM_USER_ID = "user_id";
    public final static String PARAM_VISITOR_USER_ID = "visitor_user_id";
    public final static String PARAM_NOT_USER_ID = "not_user_id";
    public final static String PARAM_FILE_ID = "file_id";
    public final static String PARAM_FROM_USER_ID = "from_user_id";
    public final static String PARAM_CHAT_MESSAGE = "chat_message";
    public final static String PARAM_FILE = "file";
    public final static String PARAM_FILES = "files";

    public final static String BASE_URL = "http://localhost:8090";

    public final static String MAIN_ICON = "/favicon";
    public final static String API_CHAT_MESSAGES = "/chat_messages";
    public final static String API_CHAT_USERS_MESSAGES = "/chat_users_messages";
    public final static String API_USER_PROFILE = "/user/profile";
    public final static String API_USER_PROFILES = "/user/profiles";
    public final static String API_POST_USER_PROFILES = API_USER_PROFILES+"/{page}/{not_user_id}";
    public final static String API_CHAT_USER_PROFILES = "/user_profiles_chats";
    public final static String API_USER_IMAGE = "/resource";
    public final static String API_USER_IMAGES = "/uploads";
    public final static String API_USER_RESOURCE_THUMB = "/resource/thumb";
    public final static String API_USER_IMAGE_THUMB = "/upload/thumb";
    public final static String API_IMAGE_THUMB = "/thumb";
    public final static String API_USER_IMAGES_ALL = "/uploads/all";
    public final static String API_USER_IMAGES_MULTI = "/uploads/multi";
    public final static String API_USER_PROFILE_USER_ID = API_USER_PROFILE+"/{"+Api.PARAM_USER_ID+"}";
    public final static String API_CHAT_ADD_ITEM = "/chat/add";
    public final static String API_CHAT_MESSAGE_APPLY = "/chat/apply/messages";
    public final static String API_CHAT_MESSAGES_STATUS = "/chat/status/messages";
    public final static String API_USER_VISITOR = "/user/visitor";

    public final static String API_RESOURCE_URI_TEMP = API_PREFIX+API_USER_IMAGE+"?user_id=%s&file_id=%s";
    public final static String API_RESOURCE_URI_THUMB = API_PREFIX+API_USER_IMAGE+"/thumb?user_id=%s";

    public final static String[] openedUrlPaths = {API_USER_IMAGE, MAIN_ICON};
}
