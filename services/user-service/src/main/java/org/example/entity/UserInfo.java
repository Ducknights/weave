package org.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("info")
public class UserInfo {
    private Long id;
    private String name;
    private String email;
    private String gender;
    private LocalDateTime birthday;
    private String address;
    private String motto;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}