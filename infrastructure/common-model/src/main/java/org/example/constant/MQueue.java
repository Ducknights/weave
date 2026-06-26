package org.example.constant;

public class MQueue {
    //交换机
    public static final String TOPIC_EXCHANGE = "topic_exchange";

    // 验证码相关队列
    public static final String CAPTCHA_ROUTING_KEY = "captcha";
    public static final String CAPTCHA_QUEUE = "captcha_queue";

    // 用户登录相关队列
    public static final String USER_LOGIN_ROUTING_KEY = "user.login";
    public static final String USER_LOGIN_QUEUE = "user_login_queue";

    // 审核相关队列
    public static final String AUDIT_ROUTING_KEY = "audit";
    public static final String AUDIT_QUEUE = "audit_queue";

    // 结果相关队列
    public static final String RESULT_ROUTING_KEY = "result";
    public static final String RESULT_QUEUE = "result_queue";

    // 帖子行为相关队列
    public static final String POST_ACTION_ROUTING_KEY = "post.action";
    public static final String POST_ACTION_QUEUE_1 = "post_action_queue_1";
    public static final String POST_ACTION_QUEUE_2 = "post_action_queue_2";
    public static final String POST_ACTION_QUEUE_3 = "post_action_queue_3";

    // 帖子同步相关队列
    public static final String POST_SYNC_ROUTING_KEY = "post.sync";
    public static final String POST_SYNC_QUEUE = "post_sync_queue";

    // 帖子缓存相关队列
    public static final String POST_CACHE_ROUTING_KEY = "post.cache";
    public static final String POST_CACHE_QUEUE = "post_cache_queue";

    // 聊天相关队列
    public static final String CHAT_PUSH_ROUTING_KEY = "chat.push";
    public static final String CHAT_PUSH_QUEUE = "chat_push_queue";
}
