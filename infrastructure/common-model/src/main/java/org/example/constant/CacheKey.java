package org.example.constant;

public class CacheKey {
    private static final String KEY_SEPARATOR = "::";
    public static final String USER_AUTHORITY_AREA = "user:authorities";
    public static final String USER_INFO_AREA = "user:info";
    public static final String CAPTCHA_AREA = "verification:code";
    public static final String ACTIVITY_AREA = "activity";
    public static final String CLUB_AREA = "club";
    public static final String POST_AREA = "post";
    public static final String POST_DETAIL_AREA = "post:detail";
    public static final String USER_LIKED_POSTS = "user:liked:posts";
    public static final String USER_COLLECTED_POSTS = "user:collected:posts";
    public static final String USER_SHARED_POSTS = "user:shared:posts";
    public static final String USER_FOLLOWERS = "user:followers";
    public static final String USER_FANS = "user:fans";
    public static final String USER_MUTED_USERS = "user:muted:users";
    public static final String USER_BLOCKED_USERS = "user:blocked:users";
    public static final String PRESENTED_URL_AREA = "presented:url";

    public static String buildCacheKey(String area, Object identifier) {
        return area + KEY_SEPARATOR + identifier;
    }
}
