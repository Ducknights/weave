package org.example.service;

import org.example.model.entity.SearchDocument;

import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口
 * 提供搜索相关的业务逻辑
 */
public interface SearchService {
    
    /**
     * 搜索内容
     *
     * @param keyword 搜索关键词
     * @param type 搜索类型
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    Map<String, Object> search(String keyword, String type, int page, int size);

    /**
     * 搜索内容（返回 Map 列表，用于 Feign 调用）
     *
     * @param keyword 搜索关键词
     * @param type 搜索类型
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果列表
     */
    List<Map<String, Object>> searchForFeign(String keyword, String type, int page, int size);
    
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
     * @param type 内容类型
     * @param id 内容ID
     * @return 是否删除成功
     */
    boolean deleteIndex(String type, Long id);
    
    /**
     * 根据类型和ID获取索引文档
     *
     * @param type 内容类型
     * @param id 内容ID
     * @return 搜索文档
     */
    SearchDocument getIndex(String type, Long id);
    
    /**
     * 批量索引内容
     *
     * @param documents 搜索文档列表
     * @return 成功索引的数量
     */
    int batchIndexContent(Iterable<SearchDocument> documents);
}
