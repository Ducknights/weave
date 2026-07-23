package org.weave.service;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import com.weave.chat.model.entity.Message;
import com.weave.chat.model.vo.ConversationVo;
import com.weave.chat.service.ConversationService;
import com.weave.chat.service.MessageService;
import com.weave.chat.service.SocketIOEventHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

@Log4j2
@SpringBootTest
class ChatServerTest {

    @MockBean
    private SocketIOEventHandler socketIOEventHandler;

    @Resource
    private MessageService messageService;
    @Resource
    private ConversationService conversationService;

    // 发送消息 测试通过
    @Test
    void saveMessage() {
        Message message = messageService.saveMessage(1L, 2L, "1");
        log.info("保存的消息: {}", message);
    }

    // 获取消息 测试通过
    @Test
    void getMessages() {
        List<Message> messages = messageService.getNewMessages(1L, 9L);
        log.info("获取的消息: {}", messages);
    }

    // 获取会话Vo
    @Test
    void getConversationVo() {
        List<ConversationVo> conversationVo = conversationService.getConversations(1L);
        log.info("获取的会话Vo: {}", conversationVo);
    }
}