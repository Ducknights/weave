package org.example.strings;

public class CacheKey {
    /**
     * 缓存键分隔符
     */
    private static final String KEY_SEPARATOR = "::";
    /**
     * 用户权限缓存区域
     */
    public static final String USER_AUTHORITY_AREA = "user:authorities";
    /**
     * 用户权限缓存区域
     */
    public static final String USER_INFO_AREA = "user:info";
    /**
     * 活动缓存区域
     */
    public static final String ACTIVITY_AREA = "Activity";
    /**
     * 用户点赞缓存区域
     */
    public static final String USER_LIKED_POSTS = "user:liked:posts";
    /**
     * 用户收藏帖子缓存区域
     */
    public static final String USER_COLLECTED_POSTS = "user:collected:posts";
    /**
     * 用户分享帖子缓存区域
     */
    public static final String USER_SHARED_POSTS = "user:shared:posts";
    /**
     * 用户关注用户缓存区域
     */
    public static final String USER_FOLLOWERS = "user:followers";
    /**
     * 用户屏蔽用户缓存区域
     */
    public static final String USER_MUTED_USERS = "user:muted:users";
    /**
     * 用户拉黑用户缓存区域
     */
    public static final String USER_BLOCKED_USERS = "user:blocked:users";

    public static String buildCacheKey(String area, Object identifier) {
        return area + KEY_SEPARATOR + identifier;
    }
}