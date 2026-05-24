package org.example.repository;

import org.example.model.entity.SearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 搜索文档仓库
 * 用于操作 Elasticsearch 索引
 */
@Repository
public interface SearchDocumentRepository extends ElasticsearchRepository<SearchDocument, String> {
    
    /**
     * 根据类型和目标ID查询文档
     */
    SearchDocument findByTypeAndTargetId(String type, Long targetId);
    
    /**
     * 根据类型查询文档列表
     */
    List<SearchDocument> findByType(String type);
    
    /**
     * 根据类型和作者ID查询文档列表
     */
    List<SearchDocument> findByTypeAndAuthorId(String type, Long authorId);
    
    /**
     * 删除指定类型和目标ID的文档
     */
    void deleteByTypeAndTargetId(String type, Long targetId);
}
