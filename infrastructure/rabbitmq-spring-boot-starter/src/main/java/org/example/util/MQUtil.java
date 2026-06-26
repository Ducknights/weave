package org.example.util;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.MQueue;
import org.example.model.PostActionMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class MQUtil {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送帖子缓存消息
     * @param postsForCache 帖子缓存数据
     */
    public void sendToPostCacheQueue(Object postsForCache) {
        try {
            rabbitTemplate.convertAndSend(
                    MQueue.TOPIC_EXCHANGE,
                    MQueue.POST_CACHE_ROUTING_KEY,
                    postsForCache
            );
        } catch (Exception e) {
            log.error("发送帖子缓存消息失败", e);
        }
    }

    /**
     * 发送验证码邮件的方法
     * 使用RabbitMQ消息队列发送验证码邮件
     * @param email 接收验证码的邮箱地址
     */
    public void sendCaptchaEmail(String email) {
        try {
            rabbitTemplate.convertAndSend(
                    MQueue.TOPIC_EXCHANGE,
                    MQueue.CAPTCHA_ROUTING_KEY,
                    email
            );
        } catch (Exception e) {
            log.error("{}的验证码发送失败", email,e);
        }
    }

    /**
     * 同步 mySQL中的数据到 ES
     */
    public void sendSyncToES(Object object){
        try{
            rabbitTemplate.convertAndSend(
                    MQueue.TOPIC_EXCHANGE,
                    MQueue.POST_SYNC_ROUTING_KEY,
                    object
            );
        }catch (Exception e){
            log.error("同步到ES失败");
        }
    }

    /**
     * 发送帖子行为消息
     */
    public void sendToPostAction(PostActionMessage message) {
        try{
            rabbitTemplate.convertAndSend(
                    MQueue.TOPIC_EXCHANGE,
                    MQueue.POST_ACTION_ROUTING_KEY,
                    message
            );
        }catch (Exception e){
            log.error("发送帖子行为消息失败");
        }
    }

    /**
     * 发送用户登录事件
     */
    public void sendUserLoginEvent(Long userId) {
        try{
            rabbitTemplate.convertAndSend(
                    MQueue.TOPIC_EXCHANGE,
                    MQueue.USER_LOGIN_ROUTING_KEY,
                    userId
            );
        }catch (Exception e){
            log.error("发送用户登录事件失败");
        }
    }

    /**
     * 推送聊天消息
     */
    public void pushChatMessage(Object message) {
        try{
            rabbitTemplate.convertAndSend(
                    MQueue.TOPIC_EXCHANGE,
                    MQueue.CHAT_PUSH_ROUTING_KEY,
                    message
            );
        }catch (Exception e){
            log.error("推送聊天消息失败");
        }
    }
}
