package org.example.model.dto;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class PostDto {
    private Long clubId;
    @NonNull
    private String title;
    private String content;
    private List<String> coverImage;
}
