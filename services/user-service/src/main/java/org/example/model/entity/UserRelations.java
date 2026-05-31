package org.example.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.OrderBy;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.model.eunms.RelationEnum;

import java.time.LocalDateTime;

/**
 * 用户关系实体类，表示用户之间的关系，如关注、屏蔽、拉黑等。
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("user_relations")
public class UserRelations {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long targetId;
    private RelationEnum type;
    @OrderBy()
    private LocalDateTime createdTime;
}
