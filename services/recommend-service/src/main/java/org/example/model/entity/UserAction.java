package org.example.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.enums.ActionEnum;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_actions")
public class UserAction {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private Long userId;
    private Long targetId;
    private ActionEnum type;
    @OrderBy()
    private LocalDateTime createdTime;
}
