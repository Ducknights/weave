package org.example.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.mapper.ConversationMemberMapper;
import org.example.model.dto.ConversationMemberParam;
import org.example.service.ConversationMemberService;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ConversationMemberServiceImpl implements ConversationMemberService {

    @Resource
    private ConversationMemberMapper conversationMemberMapper;

    @Override
    public void incrementUnreadCount(ConversationMemberParam param) {
        conversationMemberMapper.incrementUnreadCount(param);
    }

    @Override
    public Long getLastReadMessageId(ConversationMemberParam param) {
        return conversationMemberMapper.getLastReadMessageId(param);
    }

    @Override
    public void updateUserLastReadMessageId(ConversationMemberParam param, Long messageId) {
        conversationMemberMapper.updateUserLastReadMessageId(param, messageId);
    }

    @Override
    public void resetUnreadCount(ConversationMemberParam param) {
        conversationMemberMapper.resetUnreadCount(param);
    }
}
