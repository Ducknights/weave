package org.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.example.model.GenderEnum;

import java.time.LocalDateTime;

/**
 * 用户信息实体类
 */

@Data
@TableName("user_info")
public class UserInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private LocalDateTime birthday;
    private String address;
    private String motto;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}