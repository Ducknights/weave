package com.weave.chat.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName("conversation_member")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationMember {
    @TableId(type = IdType.AUTO)
    private Long id;
    // 用户ID
    private Long userId;
    // 会话ID
    private Long conversationId;
    // 最后阅读消息ID
    private Long lastReadMessageId;
    // 未读消息数
    private int unreadCount;
    // 创建时间
    private LocalDateTime createTime;
}