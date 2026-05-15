package org.example.service.impl;

import lombok.extern.log4j.Log4j2;
import org.example.entity.SearchDocument;
import org.example.repository.SearchDocumentRepository;
import org.example.service.SearchService;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索服务实现
 * 提供搜索相关的业务逻辑实现
 */
@Log4j2
@Service
public class SearchServiceImpl implements SearchService {
    
    @Resource
    private SearchDocumentRepository searchDocumentRepository;
    
    @Resource
    private ElasticsearchOperations elasticsearchOperations;

    @Override
    public Map<String, Object> search(String keyword, String type, int page, int size) {
        log.info("执行搜索: keyword={}, type={}, page={}, size={}", keyword, type, page, size);
        
        // 构建查询
        StringQuery query = new StringQuery(
                "{" +
                "  \"bool\": {" +
                "    \"should\": [" +
                "      {\"match\": {\"content\": {\"query\": \"" + keyword + "\", \"fuzziness\": \"AUTO\"}}}," +
                "      {\"match\": {\"title\": {\"query\": \"" + keyword + "\"}}}" +
                "    ]" +
                "  }" +
                "}"
        );

        // 如果指定了类型，添加类型过滤
        if (type != null && !type.isEmpty()) {
            query = new StringQuery(
                    "{" +
                    "  \"bool\": {" +
                    "    \"should\": [" +
                    "      {\"match\": {\"content\": {\"query\": \"" + keyword + "\", \"fuzziness\": \"AUTO\"}}}," +
                    "      {\"match\": {\"title\": {\"query\": \"" + keyword + "\"}}}" +
                    "    ]," +
                    "    \"must\": [" +
                    "      {\"match\": {\"type\": \"" + type + "\"}}" +
                    "    ]" +
                    "  }" +
                    "}"
            );
        }

        // 执行搜索
        SearchHits<SearchDocument> searchHits = elasticsearchOperations.search(query, SearchDocument.class);
        
        // 构建结果
        List<Map<String, Object>> items = new ArrayList<>();
        searchHits.forEach(hit -> {
            Map<String, Object> item = new HashMap<>();
            item.put("id", hit.getId());
            item.put("type", hit.getContent().getType());
            item.put("targetId", hit.getContent().getTargetId());
            item.put("title", hit.getContent().getTitle());
            item.put("content", hit.getContent().getContent());
            item.put("author", hit.getContent().getAuthor());
            item.put("authorId", hit.getContent().getAuthorId());
            item.put("createdAt", hit.getContent().getCreatedAt());
            item.put("score", hit.getScore());
            items.add(item);
        });
        
        Map<String, Object> results = new HashMap<>();
        results.put("total", searchHits.getTotalHits());
        results.put("items", items);
        results.put("page", page);
        results.put("size", size);
        results.put("keyword", keyword);
        results.put("type", type);
        
        return results;
    }

    @Override
    public boolean indexContent(SearchDocument document) {
        try {
            log.info("索引内容: type={}, targetId={}, title={}", 
                    document.getType(), document.getTargetId(), document.getTitle());
            
            // 生成文档ID: type_targetId
            String documentId = document.getType() + "_" + document.getTargetId();
            document.setId(documentId);
            
            searchDocumentRepository.save(document);
            return true;
        } catch (Exception e) {
            log.error("索引内容失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean updateIndex(SearchDocument document) {
        try {
            log.info("更新索引: type={}, targetId={}", document.getType(), document.getTargetId());
            
            // 生成文档ID: type_targetId
            String documentId = document.getType() + "_" + document.getTargetId();
            document.setId(documentId);
            
            searchDocumentRepository.save(document);
            return true;
        } catch (Exception e) {
            log.error("更新索引失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public boolean deleteIndex(String type, Long id) {
        try {
            log.info("删除索引: type={}, id={}", type, id);
            String documentId = type + "_" + id;
            searchDocumentRepository.deleteById(documentId);
            return true;
        } catch (Exception e) {
            log.error("删除索引失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public SearchDocument getIndex(String type, Long id) {
        try {
            String documentId = type + "_" + id;
            return searchDocumentRepository.findById(documentId).orElse(null);
        } catch (Exception e) {
            log.error("获取索引失败: {}", e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public int batchIndexContent(Iterable<SearchDocument> documents) {
        try {
            Iterable<SearchDocument> savedDocuments = searchDocumentRepository.saveAll(documents);
            int count = 0;
            for (SearchDocument document : savedDocuments) {
                count++;
            }
            return count;
        } catch (Exception e) {
            log.error("批量索引失败: {}", e.getMessage(), e);
            return 0;
        }
    }
}
