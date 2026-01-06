package org.example.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * 邮件服务类
 * 提供发送简单邮件、HTML邮件、带附件邮件和模板邮件的功能
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${app.email.from-name}")
    private String fromName;

    @Value("${app.email.reply-to}")
    private String replyTo;

    /**
     * 发送HTML邮件
     *
     * @param to          收件人邮箱
     * @param subject     邮件主题
     * @param htmlContent HTML格式的邮件内容
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom("2897662424@qq.com", fromName);
            helper.setTo(to);
            helper.setReplyTo(replyTo);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true表示HTML格式

            javaMailSender.send(message);
            System.out.println("HTML邮件发送成功: " + to);
        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("HTML邮件发送失败: " + e.getMessage());
            throw new RuntimeException("邮件发送失败", e);
        }
    }

    /**
     * 使用Thymeleaf模板发送HTML邮件
     *
     * @param to             收件人邮箱
     * @param subject        邮件主题
     * @param templateName   模板名称（不需要.html后缀）
     * @param contextVariables 模板变量
     */
    public void sendTemplateEmail(String to, String subject, String templateName, Map<String, Object> contextVariables) {
        try {
            Context context = new Context();
            context.setVariables(contextVariables);
            
            // 渲染模板
            String htmlContent = templateEngine.process(templateName, context);
            
            // 发送HTML邮件
            sendHtmlEmail(to, subject, htmlContent);
            
            System.out.println("模板邮件发送成功: " + to);
        } catch (Exception e) {
            System.err.println("模板邮件发送失败: " + e.getMessage());
            throw new RuntimeException("邮件发送失败", e);
        }
    }
}