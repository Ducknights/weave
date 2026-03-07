package org.example.controller;

import jakarta.annotation.Resource;
import org.example.context.UserContext;
import org.example.model.dto.SendMessageDTO;
import org.example.model.entity.Conversation;
import org.example.model.entity.Message;
import org.example.model.vo.ConversationVo;
import org.example.service.ChatServer;
import org.example.service.LongPollingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private ChatServer chatServer;
    @Resource
    private UserContext userContext;
    @Resource
    private LongPollingService longPollingService;

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    public List<ConversationVo> getConversations() {
        Long userId = userContext.getUserId();
        return chatServer.getConversations(userId);
    }

    /**
     * 创建会话
     */
    @PostMapping("/conversation")
    public Conversation creatConversation(@RequestParam Long userB) {
        Long userA = userContext.getUserId();
        return chatServer.createConversation(userA, userB);
    }

    /**
     * 获取消息列表（分页查询，用于加载历史消息）
     */
    @GetMapping("/messages")
    public List<Message> getMessages(@RequestParam Long conversationId, @RequestParam int page, @RequestParam int size) {
        return chatServer.getMessages(conversationId,page,size);
    }

    /**
     * 长轮询获取新消息（实时通知）
     * 监听用户的所有会话，返回用户ID > last_received_id的所有消息
     * @param lastReceivedId 客户端最后收到的消息ID
     */
    @GetMapping("/messages/poll")
    public List<Message> pollNewMessages(@RequestParam(required = false) Long lastReceivedId) {
        Long userId = userContext.getUserId();
        if (lastReceivedId == null) {
            lastReceivedId = 0L;
        }
        return longPollingService.longPollNewMessages(userId, lastReceivedId);
    }

    /**
     * 发送消息
     */
    @PostMapping("/message")
    public Message sendMessage(@RequestBody SendMessageDTO dto) {
        Long fromUserId = userContext.getUserId();
        return chatServer.sendMessage(fromUserId, dto.getToUserId(), dto.getContent());
    }
}

