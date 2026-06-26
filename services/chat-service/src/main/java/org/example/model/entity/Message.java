package org.example.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.enums.MessageType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("message")
@Builder
public class Message {
    @TableId(type = IdType.AUTO)
    private Long id;
    // 消息所属会话ID
    private Long conversationId;
    // 消息发送者ID
    private Long fromUserId;
    // 消息接收者ID
    private Long toUserId;
    // 消息内容
    private String content;
    // 消息类型
    private MessageType type;
    // 创建时间
    private LocalDateTime createTime;
}

