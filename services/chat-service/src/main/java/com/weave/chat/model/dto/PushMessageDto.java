package com.weave.chat.model.dto;

import com.weave.chat.model.entity.Message;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PushMessageDto {
    // 接收者ID
    private Long toId;
    // 消息内容
    private Message message;
}
