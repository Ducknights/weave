package com.weave.rabbitmq.constant;

public class MQueue {
    //交换机
    public static final String TOPIC_EXCHANGE = "topic_exchange";
    public static final String DLX_EXCHANGE = "dlx_exchange";

    // 验证码相关队列
    public static final String CAPTCHA_ROUTING_KEY = "captcha";
    public static final String CAPTCHA_QUEUE = "captcha_queue";

    // 用户登录相关队列
    public static final String USER_CACHE_ROUTING_KEY = "user.cache";
    public static final String USER_CACHE_QUEUE = "user_cache_queue";
    public static final String USER_CACHE_DLQ = "user_cache_queue_dlq";
    public static final String USER_CACHE_DLQ_ROUTING_KEY = "user.cache.dlq";

    // 审核相关队列
    public static final String AUDIT_ROUTING_KEY = "audit";
    public static final String AUDIT_QUEUE = "audit_queue";
    public static final String AUDIT_DLQ = "audit_queue_dlq";
    public static final String AUDIT_DLQ_ROUTING_KEY = "audit.dlq";

    // 结果相关队列
    public static final String RESULT_ROUTING_KEY = "result";
    public static final String RESULT_QUEUE = "result_queue";
    public static final String RESULT_DLQ = "result_queue_dlq";
    public static final String RESULT_DLQ_ROUTING_KEY = "result.dlq";

    // 帖子行为相关队列
    public static final String POST_ACTION_ROUTING_KEY = "post.action";
    public static final String POST_ACTION_QUEUE_1 = "post_action_queue_1";
    public static final String POST_ACTION_QUEUE_2 = "post_action_queue_2";
    public static final String POST_ACTION_QUEUE_3 = "post_action_queue_3";
    public static final String POST_ACTION_DLQ_1 = "post_action_queue_1_dlq";
    public static final String POST_ACTION_DLQ_2 = "post_action_queue_2_dlq";
    public static final String POST_ACTION_DLQ_3 = "post_action_queue_3_dlq";
    public static final String POST_ACTION_DLQ_ROUTING_KEY = "post.action.dlq";

    // 帖子同步相关队列
    public static final String POST_SYNC_ROUTING_KEY = "post.sync";
    public static final String POST_SYNC_QUEUE = "post_sync_queue";
    public static final String POST_SYNC_DLQ = "post_sync_queue_dlq";
    public static final String POST_SYNC_DLQ_ROUTING_KEY = "post.sync.dlq";

    // 草稿审核通过发布相关队列
    public static final String DRAFT_PUBLISH_ROUTING_KEY = "draft.publish";
    public static final String DRAFT_PUBLISH_QUEUE = "draft_publish_queue";
    public static final String DRAFT_PUBLISH_DLQ = "draft_publish_queue_dlq";
    public static final String DRAFT_PUBLISH_DLQ_ROUTING_KEY = "draft.publish.dlq";

    // 草稿发布结果回执相关队列
    public static final String DRAFT_PUBLISH_RESULT_ROUTING_KEY = "draft.publish.result";
    public static final String DRAFT_PUBLISH_RESULT_QUEUE = "draft_publish_result_queue";
    public static final String DRAFT_PUBLISH_RESULT_DLQ = "draft_publish_result_queue_dlq";
    public static final String DRAFT_PUBLISH_RESULT_DLQ_ROUTING_KEY = "draft.publish.result.dlq";

    // 帖子缓存相关队列
    public static final String POST_CACHE_ROUTING_KEY = "post.cache";
    public static final String POST_CACHE_QUEUE = "post_cache_queue";
    public static final String POST_CACHE_DLQ = "post_cache_queue_dlq";
    public static final String POST_CACHE_DLQ_ROUTING_KEY = "post.cache.dlq";

    // 聊天相关队列
    public static final String CHAT_PUSH_ROUTING_KEY = "chat.push";
    public static final String CHAT_PUSH_QUEUE = "chat_push_queue";
}
