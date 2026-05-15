package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostActionMessage {
    private Long userId;
    private Long postId;
    private String action; // like, favorite, share
    private Boolean increment; // true: 增加, false: 减少
}
