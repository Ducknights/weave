package org.example.service;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatServerTest {
    @Autowired
    private ChatServer chatServer;

    @Test
    void sendMessage() {
        System.out.println(chatServer.sendMessage(1L, 2L, "Hello, World!"));
    }
}