package org.example.consumer;

import lombok.extern.log4j.Log4j2;
import org.example.service.EmailService;
import org.example.constant.MQueue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
     * 邮件控制器
     * 提供RESTful API接口和RabbitMQ队列来发送各种类型的邮件
     */
@Log4j2
@RestController
public class EmailConsumer {

    @Autowired
    private EmailService emailService;

    /**
     * 使用RabbitMQ监听队列的方法，处理发送验证码邮件的请求
     * 并将生成的验证码缓存到指定区域
     *
     * @param email 接收验证码的邮箱地址
     */
    @RabbitListener(queues = MQueue.CAPTCHA_QUEUE)
    public void sendVerificationEmail(String email) {
        log.info("发送验证码邮件给：{}", email);
        emailService.sendVerificationCodeEmail(email);
    }

    /**
     * 健康检查接口
     * GET /api/captcha/health
     */
    @GetMapping("/api/captcha/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "message", "服务运行正常",
                "timestamp", System.currentTimeMillis()));
    }
}