package org.example.service.impl;

import jakarta.annotation.Resource;
import org.example.mapper.MessageMapper;
import org.example.model.entity.Message;
import org.example.model.enums.MessageType;
import org.example.service.ConversationMemberService;
import org.example.service.ConversationService;
import org.example.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    @Resource
    private ConversationService conversationService;
    @Resource
    private ConversationMemberService conversationMemberService;
    @Resource
    private MessageMapper messageMapper;

    @Override
    public Message saveMessage(Long fromId, Long toId, String content) {
        // 获取会话ID,没有则创建
        Long conversationId = conversationService.getOrCreatePrivateConversation(fromId, toId);
        // 构造消息
        Message message = Message.builder()
                .conversationId(conversationId)
                .fromUserId(fromId)
                .toUserId(toId)
                .content(content)
                // TODO: 希望支持不同类型消息
                .type(MessageType.TEXT)
                .createTime(LocalDateTime.now())
                .build();
        // 保存消息到数据库
        messageMapper.insert(message);
        // 更新会话表(最新消息内容)
        conversationService.updateConversation(conversationId, message.getContent());
        // 更新会话成员表(最新消息内容和时间)
        conversationMemberService.updateUserLastReadMessageId(conversationId, fromId, message.getId());
        // 更新会话成员表(给其他成员的未读数+1)
        conversationMemberService.updatePrivateConversationUser(conversationId,toId);
        return message;
    }

    /**
     * 按照会话ID和用户ID获取消息
     */
    @Override
    public List<Message> getMessages(Long userId, Long conversationId, int page, int size) {
        return messageMapper.selectLastN(userId, conversationId, page, size);
    }

    /**
     * 获取未读消息
     */
    @Override
    public List<Message> getNewMessages(Long userId, Long conversationId) {
        Long lastReadMessageId = conversationMemberService.getLastReadMessageId(userId, conversationId);
        List<Message> newMessages = messageMapper.selectNewMessages(userId, conversationId, lastReadMessageId);
        if (!newMessages.isEmpty()) {
            conversationMemberService.updateUserLastReadMessageId(conversationId, userId, newMessages.get(0).getId());
        }
        conversationMemberService.resetUnreadCount(conversationId, userId);
        return newMessages;
    }
}
