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
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation")
@Builder
public class Conversation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userSmallId;
    private Long userBigId;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private LocalDateTime createTime;
}


