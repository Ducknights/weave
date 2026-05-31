package org.example.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.model.eunms.GenderEnum;

import java.time.LocalDateTime;

/**
 * 用户信息实体类
 */
@Data
@TableName("user_info")
public class UserInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    @Size(min = 1, max = 15)
    private String name;
    private String avatar;
    @Email
    private String email;
    private GenderEnum gender;
    private LocalDateTime birthday;
    private String address;
    private String motto;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}