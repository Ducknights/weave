package com.weave.chat.service;

import com.weave.chat.model.entity.Message;

import java.util.List;

public interface MessageService {

    Message saveMessage(Long fromId, Long toId, String content);

    List<Message> getMessages(Long userId, Long conversationId, int page, int size);

    List<Message> getNewMessages(Long userId, Long conversationId);

    Long getNewMessageId(Long conversationId);
}
