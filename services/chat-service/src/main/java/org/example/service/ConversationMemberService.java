package org.example.service;

public interface ConversationMemberService {
    void updatePrivateConversationUser(Long conversationId,Long toId);

    Long getLastReadMessageId(Long userId, Long conversationId);

    void updateUserLastReadMessageId(Long conversationId, Long userId, Long messageId);

    void resetUnreadCount(Long userId, Long conversationId);
}
