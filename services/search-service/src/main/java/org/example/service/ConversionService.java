package org.example.service;

import org.example.dto.PostDetailVo;
import org.example.model.dto.SearchResultDto;

import java.util.List;

public interface ConversionService {

    /**
     * 将搜索结果转换为帖子详情列表
     *
     * @param results ES搜索结果（包含id和分数）
     * @return 帖子详情列表（已设置搜索分数，保持ES排序）
     */
    List<PostDetailVo> convertToPostDetailVo(List<SearchResultDto> results);
}
