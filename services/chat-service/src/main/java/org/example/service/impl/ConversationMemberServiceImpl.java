package org.example.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.mapper.ConversationMemberMapper;
import org.example.service.ConversationMemberService;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ConversationMemberServiceImpl implements ConversationMemberService {

    @Resource
    private ConversationMemberMapper conversationMemberMapper;

    /**
     * 未读消息数+1
     * @param conversationId 会话ID
     * @param toId 用户ID
     */
    @Override
    public void updatePrivateConversationUser(Long conversationId, Long toId) {
        conversationMemberMapper.incrementUnreadCount(conversationId, toId);
    }

    /**
     * 获取最后阅读消息ID
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @return 最后阅读消息ID
     */
    @Override
    public Long getLastReadMessageId(Long userId, Long conversationId) {
        return conversationMemberMapper.getLastReadMessageId(userId, conversationId);
    }

    /**
     * 更新用户最后阅读消息ID
     * @param conversationId 会话ID
      * @param userId 用户ID
     * @param messageId 消息ID
     */
    @Override
    public void updateUserLastReadMessageId(Long conversationId, Long userId, Long messageId) {
        log.info("Updating user last read message ID for conversation {} and user {} with message ID {}", conversationId, userId, messageId);
        conversationMemberMapper.updateUserLastReadMessageId(conversationId, userId, messageId);
    }

    /**
     * 清空会话未读消息计数
     * @param userId 用户ID
     * @param conversationId 会话ID
     */
    @Override
    public void resetUnreadCount(Long userId, Long conversationId) {
        conversationMemberMapper.resetUnreadCount(userId, conversationId);
    }
}
