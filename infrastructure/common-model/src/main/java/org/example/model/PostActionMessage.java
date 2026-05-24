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
    private String action;
    private Boolean increment;
}
