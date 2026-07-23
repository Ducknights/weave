package com.weave.search.service;

import com.weave.search.model.dto.SearchResultDto;
import com.weave.search.model.entity.SearchDocument;

import java.util.List;

/**
 * 搜索服务接口
 * 提供搜索相关的业务逻辑
 */
public interface SearchService {
    
    /**
     * 搜索内容
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果（仅包含ID和分数，以及总数）
     */
    List<SearchResultDto> search(String keyword, int page, int size);

    /**
     * 索引内容（用于将内容添加到搜索索引）
     *
     * @param document 搜索文档
     */
    void indexContent(SearchDocument document);
    
    /**
     * 更新索引
     *
     * @param document 搜索文档
     */
    void updateIndex(SearchDocument document);
    
    /**
     * 删除索引
     *
     * @param id 内容ID
     */
    void deleteIndex(Long id);

    /**
     * 隐藏索引
     *
     * @param id 内容ID
     */
    void hideIndex(Long id);

    /**
     * 恢复索引
     *
     * @param id 内容ID
     */
    void restoreIndex(Long id);
}
