package org.example.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.ActionEnum;

import java.time.LocalDateTime;

/**
 * 用户动作实体类，表示用户对目标执行的动作，如点赞、收藏、分享等。
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_actions")
public class UserActions {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long targetId;
    private ActionEnum type;
    @OrderBy()
    private LocalDateTime createdTime;
}
