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
     * @return 是否索引成功
     */
    boolean indexContent(SearchDocument document);
    
    /**
     * 更新索引
     *
     * @param document 搜索文档
     * @return 是否更新成功
     */
    boolean updateIndex(SearchDocument document);
    
    /**
     * 删除索引
     *
     * @param id 内容ID
     * @return 是否删除成功
     */
    boolean deleteIndex(Long id);
    
    /**
     * 根据ID获取索引文档
     *
     * @param id 内容ID
     * @return 搜索文档
     */
    SearchDocument getIndex(Long id);
    
    /**
     * 批量索引内容
     *
     * @param documents 搜索文档列表
     * @return 成功索引的数量
     */
    int batchIndexContent(Iterable<SearchDocument> documents);
}
