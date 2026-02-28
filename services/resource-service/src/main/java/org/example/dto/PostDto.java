package org.example.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record PostDto(
        @NotBlank(message = "标题不能为空")
        String title,
        String content,
        List<String> Urls,
        List<String> tags) {
}
