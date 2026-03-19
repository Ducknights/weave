package org.example.model.entity;

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
    private Long id;
    // 用户ID
    private Long userId;
    // 会话ID
    private Long conversationId;
    // 未读消息数
    private int unreadCount;
    // 最后阅读消息ID
    private LocalDateTime createTime;
}