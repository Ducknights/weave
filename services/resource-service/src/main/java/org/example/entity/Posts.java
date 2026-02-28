package org.example.entity;

import lombok.Data;

@Data
public class Posts {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private Long createTime;
    private Long updateTime;
    private int status; // 0: 待审核, 1: 审核通过, 2: 审核拒绝, 3: 隐藏, 4: 删除
}
