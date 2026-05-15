package org.example.util;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.MQueue;
import org.example.dto.UserBriefDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class MQUtil {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息到指定路由键
     */
    public void send(String routingKey, Object message) {
        try {
            rabbitTemplate.convertAndSend(
                    MQueue.TOPIC_EXCHANGE,
                    routingKey,
                    message
            );
        } catch (Exception e) {
            log.error("发送消息失败，routingKey={}", routingKey, e);
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
     * 用户修改信息
     *
     */
    // TODO:未来可期
    public void sendAuthUpdate(UserBriefDto dto){
        try{
            rabbitTemplate.convertAndSend(
                    MQueue.TOPIC_EXCHANGE,
                    MQueue.AUTH_QUEUE_KEY,
                    dto
            );
        }catch (Exception e){
            log.error("{}登录时的基本信息更新失败",dto.getId(),e);
        }
    }

    /**
     * 同步 mySQL中的数据到 ES
     */
    public void sendSyncToES(Object object){
        try{
            rabbitTemplate.convertAndSend(
                    MQueue.TOPIC_EXCHANGE,
                    MQueue.AUDIT_ROUTING_KEY,
                    object
            );
        }catch (Exception e){
            log.error("同步到ES失败");
        }
    }
}
