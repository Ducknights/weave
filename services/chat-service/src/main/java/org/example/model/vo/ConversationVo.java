package org.example.model.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ConversationVo {
    //会话id
    private Long id;
    //用户id
    private Long userId;
    //对方用户id
    private Long otherUserId;
    //对方用户昵称
    private String otherUserNickname;
    //对方用户头像
    private String otherUserAvatar;
    //最后一条消息
    private String lastMessage;
    //最后一条消息时间
    private LocalDateTime lastMessageTime;
}
