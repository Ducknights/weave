package org.example.model.vo;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ClubCardVo {
    private Integer id;
    private String name;
    private String description;
    private Integer memberCount;
}
