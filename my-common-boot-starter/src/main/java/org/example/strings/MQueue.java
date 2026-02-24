package org.example.strings;

public class MQueue {
    // 交换机
    public static final String TOPIC_EXCHANGE = "topic_exchange";
    // 验证路由键和队列
    public static final String CAPTCHA_ROUTING_KEY = "captcha";

    public static final String CAPTCHA_QUEUE = "captcha_queue";
    // 审核路由键和队列
    public static final String AUDIT_IMAGE_ROUTING_KEY = "image";
    public static final String AUDIT_VIDEO_ROUTING_KEY = "video";
    public static final String AUDIT_QUEUE = "audit_image_queue";
    public static final String AUDIT_VIDEO_QUEUE = "audit_video_queue";
    // 结果通知路由键
    public static final String RESULT_ROUTING_KEY = "result";
    public static final String RESULT_QUEUE = "result_queue";
}
