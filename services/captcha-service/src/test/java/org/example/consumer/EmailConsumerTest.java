package org.example.consumer;

import org.example.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class EmailConsumerTest {

    @Autowired
    private EmailService emailService;

    @Test
    void sendVerificationEmail() {
        emailService.sendVerificationCodeEmail("2897662424@qq.com");
    }
}