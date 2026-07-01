package org.example.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 搜索分页结果DTO
 * 包含搜索结果列表和总数
 */
@Data
@Builder
public class SearchPageDto {
    private String keyword;             // 搜索关键词
    private List<PostDetailVo> posts;  // 搜索结果列表
    private Integer total;                   // 总数
    private Integer pageNum;               // 当前页码
    private Integer pageSize;              // 每页大小
}