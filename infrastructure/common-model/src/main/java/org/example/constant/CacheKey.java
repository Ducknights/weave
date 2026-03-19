package org.example.constant;

public class CacheKey {
    private static final String KEY_SEPARATOR = "::";
    // 用户权限缓存区域
    public static final String USER_AUTHORITY = "user:authorities";
    // 用户信息缓存区域
    public static final String USER_INFO = "user:info";
    // 用户在线状态区域
    public static final String USER_ONLINE ="user:online";
    // 验证码缓存区域
    public static final String CAPTCHA = "verification:code";
    // 活动缓存区域
    public static final String ACTIVITY = "activity";
    // 社区缓存区域
    public static final String CLUB = "club";
    // 帖子缓存区域
    public static final String POST = "post";
    // 帖子详情缓存区域
    public static final String POST_DETAIL = "post:detail";
    // 用户点赞的帖子缓存区域
    public static final String USER_LIKED_POSTS = "user:liked:posts";
    // 用户收藏的帖子缓存区域
    public static final String USER_FAVORITE_POSTS = "user:favorite:posts";
    // 用户分享的帖子缓存区域
    public static final String USER_SHARED_POSTS = "user:shared:posts";
    // 用户关注的用户缓存区域
    public static final String USER_FOLLOWERS = "user:followers";
    // 用户的粉丝缓存区域
    public static final String USER_FANS = "user:fans";
    // 用户静音的用户缓存区域
    public static final String USER_MUTED_USERS = "user:muted:users";
    // 用户封禁的用户缓存区域
    public static final String USER_BLOCKED_USERS = "user:blocked:users";
    // 预签名URL缓存区域
    public static final String PRESENTED_URL_AREA = "presented:url";
    // 会话列表缓存区域
    public static final String CONVERSATION_LIST = "conversation:list";

    public static String buildCacheKey(String area, Object identifier) {
        return area + KEY_SEPARATOR + identifier;
    }
}
