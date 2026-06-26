package org.example.service;

import lombok.extern.log4j.Log4j2;
import org.example.model.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

@Log4j2
@SpringBootTest
class ChatServerTest {

    @MockBean
    private SocketIOEventHandler socketIOEventHandler;

    @Autowired
    private MessageService messageService;

    // 测试通过
    @Test
    void saveMessage() {
        Message message = messageService.saveMessage(1L, 2L, "1");
        log.info("保存的消息: {}", message);
    }

    // 测试通过
    @Test
    void getMessages() {
        List<Message> messages = messageService.getNewMessages(1L, 9L);
        log.info("获取的消息: {}", messages);
    }
}