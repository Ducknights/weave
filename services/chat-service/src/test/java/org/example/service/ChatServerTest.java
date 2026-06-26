package org.example.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatServerTest {

    @Autowired
    private ChatServer chatServer;

    @Test
    void sendMessage() {
    }
}