package org.example.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConversationMemberParam {
    // 会话ID
    private final Long conversationId;
    // 用户ID
    private final Long userId;
}
