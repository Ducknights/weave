package org.example.controller;

import jakarta.annotation.Resource;
import org.example.model.ApiResult;
import org.example.model.dto.SendMessageDTO;
import org.example.model.entity.Message;
import org.example.model.enums.ChatApiStatus;
import org.example.model.vo.ConversationVo;
import org.example.service.ChatServer;
import org.example.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Resource
    private ChatServer chatServer;

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    public ResponseEntity<ApiResult<List<ConversationVo>>> getConversations() {
        Long userId = SecurityUtils.getCurrentUserId();
        List<ConversationVo> conversations = chatServer.getConversations(userId);
        return ResponseEntity.ok(ChatApiStatus.GET_CONVERSATIONS_SUCCESS.response(conversations));
    }

    /**
     * 获取消息列表（分页查询，用于加载历史消息）
     */
    @GetMapping("/conversation/{conversationId}/messages")
    public ResponseEntity<ApiResult<List<Message>>> getMessages(@PathVariable Long conversationId, @RequestParam int page, @RequestParam int size) {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Message> messages = chatServer.getMessages(userId,conversationId, page, size);
        return ResponseEntity.ok(ChatApiStatus.GET_MESSAGES_SUCCESS.response(messages));
    }
}
