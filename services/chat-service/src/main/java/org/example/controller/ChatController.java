package org.example.controller;

import jakarta.annotation.Resource;
import org.example.model.entity.Message;
import org.example.model.enums.ChatApiStatus;
import org.example.model.vo.ConversationVo;
import org.example.service.ConversationMemberService;
import org.example.service.ConversationService;
import org.example.service.MessageService;
import org.example.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Resource
    private ConversationService conversationService;
    @Resource
    private ConversationMemberService conversationMemberService;
    @Resource
    private MessageService messageService;


    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<ConversationVo> conversations = conversationService.getConversations(userId);
        return ResponseEntity.ok(ChatApiStatus.GET_CONVERSATIONS_SUCCESS.response(conversations));
    }

    /**
     * 获取会话未读信息
     * 清除未读数量
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<?> getConversation(@PathVariable Long conversationId) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Message> newMessages = messageService.getNewMessages(userId, conversationId);
        return ResponseEntity.ok(ChatApiStatus.GET_MESSAGES_SUCCESS.response(newMessages));
    }

    /**
     * 获取消息列表（分页查询，用于加载历史消息）
     */
    @GetMapping("/conversation/{conversationId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable Long conversationId, @RequestParam int page, @RequestParam int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Message> messages = messageService.getMessages(userId,conversationId, page, size);
        return ResponseEntity.ok(ChatApiStatus.GET_MESSAGES_SUCCESS.response(messages));
    }


}
