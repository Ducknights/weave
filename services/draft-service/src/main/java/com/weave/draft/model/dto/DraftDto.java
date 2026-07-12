package com.weave.draft.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.List;

/**
 * 草稿请求 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DraftDto {
    private Long clubId;
    @NonNull
    private String title;
    private String content;
    private List<String> coverImage;
}
