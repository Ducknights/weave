package org.example.util;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.strings.MQueue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@ConditionalOnClass(RabbitTemplate.class)
public class MQService {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送验证码邮件的方法
     * 使用RabbitMQ消息队列发送验证码邮件
     * @param email 接收验证码的邮箱地址
     */
    public void sendCaptchaEmail(String email) {
        try {
            rabbitTemplate.convertAndSend(
                    MQueue.CAPTCHA_QUEUE,
                    MQueue.CAPTCHA_ROUTING_KEY,
                    email
            );
        } catch (Exception e) {
            log.error("验证码发送失败", e);
        }
    }
}
