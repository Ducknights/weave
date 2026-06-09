package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.constant.PostOperation;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostActionMessage {
    private Long userId;    // 谁
    private Long postId;    // 对哪个帖子
    private String action;    // 干了什么
}
