package com.weave.post.model.constant;

import java.time.Duration;

/**
 * 缓存规范
 */
public final class CacheSpec {

    private CacheSpec() {}

    /**
     * 帖子缓存
     */
    public static final class PostHash {
        public static final String POST_DETAIL = "post:detail";
        public static final Duration TTL = Duration.ofMinutes(5);
    }

    /**
     * 帖子资源url缓存
     */
    public static final class PostResourceUrl {
        public static final String POST_RESOURCE_URL = "post:resource:url";
        public static final Duration TTL = Duration.ofMinutes(5);
    }
}
