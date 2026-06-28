package org.example.model.dto;

import lombok.Data;

@Data
public class SendMessageDTO {
    // 发送者ID
    private Long fromId;
    // 接收者ID
    private Long toId;
    // 消息内容
    private String content;
}
