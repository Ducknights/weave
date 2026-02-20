package org.example.model.dto;

import lombok.Data;

@Data
public class SendMessageDTO {
    // 发送者ID
    private Long fromUserId;
    // 接收者ID
    private Long toUserId;
    // 消息内容
    private String content;
}
