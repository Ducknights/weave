package org.example.controller;

import lombok.extern.log4j.Log4j2;
import org.example.service.EmailService;
import org.example.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 邮件控制器
 * 提供RESTful API接口来发送各种类型的邮件
 */
@Log4j2
@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailController {

    @Autowired
    private EmailService emailService;

    /**
     * 发送验证码邮件
     * POST /api/email/template
     * 请求参数：
     * {
     * "to": "收件人邮箱",
     * "verificationCode": "6位验证码"
     * }
     */
    @PostMapping("/template")
    public ResponseEntity<Map<String, Object>> sendVerificationEmail(
            @RequestBody Map<String, Object> request) {
        try {
            String to = (String) request.get("to");
            // 生成6位验证码
            int verificationCode = ThreadLocalRandom.current().nextInt(100000, 1000000);
            // 验证邮箱
            if (to == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false));
            }

            // 验证邮箱格式
            if (!ValidationUtil.isValidEmail(to)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "邮箱格式不正确"));
            }

            // 准备模板变量，只传递验证码
            Map<String, Object> contextVariables = new HashMap<>();
            contextVariables.put("verificationCode", verificationCode);

            // 发送模板邮件
            emailService.sendTemplateEmail(to, "邮箱验证码", "email-template", contextVariables);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "验证码邮件发送成功"));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "邮件发送失败: " + e.getMessage()));
        }
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