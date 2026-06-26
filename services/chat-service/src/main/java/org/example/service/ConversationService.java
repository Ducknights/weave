package org.example.service;

import org.example.model.vo.ConversationVo;

import java.util.List;

public interface ConversationService {

    List<ConversationVo> getConversations(Long userId);

    Long getOrCreatePrivateConversation(Long userId1, Long userId2);

    void updateConversation(Long conversationId, String content);
}
