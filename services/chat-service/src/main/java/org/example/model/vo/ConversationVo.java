package org.example.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
public class ConversationVo {
    // 会话id
    @TableId(type = IdType.AUTO)
    private Long id;
    // 用户id
    private Long userId;
    // 对方用户id
    private Long otherUserId;
    // 对方用户昵称
    private String otherUserNickname;
    // 对方用户头像
    private String otherUserAvatar;
    // 最后一条消息
    private String lastMessage;
    // 最后一条消息时间
    @OrderBy
    private LocalDateTime lastMessageTime;
    // 未读消息数
    private Integer unreadMessageCount;
    // 是否在线
    private Boolean online;
}
