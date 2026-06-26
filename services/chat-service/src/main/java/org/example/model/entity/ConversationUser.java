package org.example.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("conversation_user")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    // 用户ID
    private Long userId;
    // 会话ID
    private Long conversationId;
    //最后消息
    private String lastMessage;
    // 最后消息时间
    private LocalDateTime lastMessageTime;
    // 未读消息数
    private int unreadCount;
    // 创建时间
    private LocalDateTime createTime;
}