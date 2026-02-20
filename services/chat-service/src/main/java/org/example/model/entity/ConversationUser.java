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
    private Long userId;
    private Long conversationId;
    private int unreadCount;
    private Long lastReadMessageId;
    private LocalDateTime createTime;
}