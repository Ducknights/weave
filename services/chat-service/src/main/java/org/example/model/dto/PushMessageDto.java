package org.example.model.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.model.entity.Message;

@Getter
@Builder
public class PushMessageDto {
    // 接收者ID
    private Long toId;
    // 消息内容
    private Message message;
}
