package org.example.service.impl;

import jakarta.annotation.Resource;
import org.example.mapper.MessageMapper;
import org.example.model.dto.ConversationMemberParam;
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
        Long conversationId = conversationService.getOrCreatePrivateConversation(fromId, toId);

        Message message = Message.builder()
                .conversationId(conversationId)
                .fromUserId(fromId)
                .toUserId(toId)
                .content(content)
                .type(MessageType.TEXT)
                .createTime(LocalDateTime.now())
                .build();
        messageMapper.insert(message);

        conversationService.updateConversation(conversationId, message.getContent());

        conversationMemberService.updateUserLastReadMessageId(
                ConversationMemberParam.builder().conversationId(conversationId).userId(fromId).build(),
                message.getId());

        conversationMemberService.incrementUnreadCount(
                ConversationMemberParam.builder().conversationId(conversationId).userId(toId).build());

        return message;
    }

    @Override
    public List<Message> getMessages(Long userId, Long conversationId, int page, int size) {
        return messageMapper.selectLastN(userId, conversationId, page, size);
    }

    @Override
    public List<Message> getNewMessages(Long userId, Long conversationId) {
        ConversationMemberParam param = ConversationMemberParam.builder()
                .conversationId(conversationId).userId(userId).build();
        // 获取上次已读的消息ID
        Long lastReadMessageId = conversationMemberService.getLastReadMessageId(param);
        // 获取新消息
        List<Message> newMessages = messageMapper.selectNewMessages(userId, conversationId, lastReadMessageId);
        if (!newMessages.isEmpty()) {
            // 更新已读消息ID
            conversationMemberService.updateUserLastReadMessageId(param, newMessages.get(0).getId());
        }
        // 重置未读消息计数
        conversationMemberService.resetUnreadCount(param);
        return newMessages;
    }

    @Override
    public Long getNewMessageId(Long conversationId) {
        return messageMapper.selectNewMessageId(conversationId);
    }
}
