package org.example.consumer;

import lombok.extern.log4j.Log4j2;
import org.example.service.EmailService;
import org.example.util.ValidationUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.example.config.RabbitMQConfig.CAPTCHA_QUEUE;

/**
 * 邮件控制器
 * 提供RESTful API接口来发送各种类型的邮件
 */
@Log4j2
@Component
public class EmailConsumer {

    @Autowired
    private EmailService emailService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 发送验证码邮件
     * POST /api/email/template
     * 请求参数：
     * {
     * "to": "收件人邮箱",
     * "verificationCode": "6位验证码"
     * }
     */
    @RabbitListener(queues = CAPTCHA_QUEUE)
    public void sendVerificationEmail(String email) {
        // 生成6位验证码
        int verificationCode = ThreadLocalRandom.current().nextInt(100000, 1000000);

        // 准备模板变量，只传递验证码
        Map<String, Object> contextVariables = new HashMap<>();
        contextVariables.put("verificationCode", verificationCode);

        // 发送模板邮件
        emailService.sendTemplateEmail(email, "邮箱验证码", "email-template", contextVariables);
        redisTemplate.opsForValue().set(email, verificationCode, 1000 * 60 * 5, TimeUnit.MILLISECONDS);
    }

    /**
     * 健康检查接口
     * GET /api/email/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "邮件服务运行正常",
                "timestamp", System.currentTimeMillis()));
    }
}