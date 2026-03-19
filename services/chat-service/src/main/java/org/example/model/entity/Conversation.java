package org.example.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation")
@Builder
public class Conversation {
    @TableId(type = IdType.AUTO)
    private Long id;
    // 小ID
    private Long userSmallId;
    // 大ID
    private Long userBigId;
    // 最后一条消息
    private String lastMessage;
    // 最后一条消息时间
    @OrderBy
    private LocalDateTime lastMessageTime;
    // 创建时间
    private LocalDateTime createTime;
}


