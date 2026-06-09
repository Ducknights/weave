package org.example.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 搜索结果DTO
 * 只包含从ES返回的ID和相关性分数
 */
@Data
@Builder
public class SearchResultDto {
    private Long id;          // 内容ID
    private Float score;      // 相关性分数
}