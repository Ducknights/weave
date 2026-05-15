package org.example.constant;

public class MQueue {
    public static final String TOPIC_EXCHANGE = "topic_exchange";

    // 验证码相关队列
    public static final String CAPTCHA_ROUTING_KEY = "captcha";
    public static final String CAPTCHA_QUEUE = "captcha_queue";

    // 审核相关队列
    public static final String AUDIT_ROUTING_KEY = "audit";
    public static final String AUDIT_QUEUE = "audit_queue";

    // 结果相关队列
    public static final String RESULT_ROUTING_KEY = "result";
    public static final String RESULT_QUEUE = "result_queue";

    // 认证相关队列
    public static final String AUTH_QUEUE = "auth_queue";
    public static final String AUTH_QUEUE_KEY = "auth";

    // 帖子行为相关队列
    public static final String POST_ACTION_QUEUE = "post_action_queue";
    public static final String POST_ACTION_ROUTING_KEY = "post.action";

    // 帖子同步相关队列
    public static final String POST_SYNC_QUEUE = "post_sync_queue";
    public static final String POST_SYNC_ROUTING_KEY = "post.sync";
}
