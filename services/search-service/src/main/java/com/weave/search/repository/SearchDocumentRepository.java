package com.weave.search.repository;

import com.weave.search.model.entity.SearchDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * 搜索文档仓库
 * 用于操作 Elasticsearch 索引
 */
@Repository
public interface SearchDocumentRepository extends ElasticsearchRepository<SearchDocument, Long> {
}
