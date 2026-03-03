package org.example.entity;

import lombok.Data;
import org.example.model.InteractionEnum;

@Data
public class UserUser {
    private Long id;
    private Long userId;
    private Long followId;
    private InteractionEnum status;
}
