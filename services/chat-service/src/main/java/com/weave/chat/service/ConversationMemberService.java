package com.weave.chat.service;

import com.weave.chat.model.dto.ConversationMemberParam;

public interface ConversationMemberService {

    // 增加未读数
    void incrementUnreadCount(ConversationMemberParam param);

    // 获取最后阅读的消息id
    Long getLastReadMessageId(ConversationMemberParam param);

    // 更新用户最后阅读消息id
    void updateUserLastReadMessageId(ConversationMemberParam param, Long messageId);

    // 重置未读数
    void resetUnreadCount(ConversationMemberParam param);
}
