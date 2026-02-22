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

    public static String buildCacheKey(String area, Object identifier) {
        return area + KEY_SEPARATOR + identifier;
    }
}
