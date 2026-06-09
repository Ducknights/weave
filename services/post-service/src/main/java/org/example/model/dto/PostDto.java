package org.example.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class PostDto {
    private Long clubId;
    private String title;
    private String content;
    private List<String> coverImage;
}
