package com.weave.search.service.impl;

import com.weave.search.model.entity.SearchDocument;
import com.weave.search.repository.SearchDocumentRepository;
import com.weave.search.service.SearchService;
import lombok.extern.log4j.Log4j2;
import com.weave.search.model.dto.SearchResultDto;
import org.jetbrains.annotations.NotNull;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    public List<SearchResultDto> search(String keyword, int page, int size) {
        log.info("执行搜索: keyword={}, page={}, size={}", keyword, page, size);

        NativeQuery query = buildSearchQuery(keyword, page, size);
        SearchHits<SearchDocument> searchHits = elasticsearchOperations.search(query, SearchDocument.class);

        List<SearchResultDto> results = new ArrayList<>();
        searchHits.forEach(hit -> {
            SearchResultDto result = SearchResultDto.builder()
                    .id(Long.valueOf(hit.getId()))
                    .score(hit.getScore())
                    .build();
            results.add(result);
        });

        return results;
    }

    private @NotNull NativeQuery buildSearchQuery(String keyword, int page, int size) {
        BoolQuery.Builder boolQueryBuilder = new BoolQuery.Builder();

        boolQueryBuilder.should(MatchQuery.of(m -> m
                .field("content")
                .query(keyword)
                .fuzziness("AUTO"))._toQuery());

        boolQueryBuilder.should(MatchQuery.of(m -> m
                .field("title")
                .query(keyword))._toQuery());

        boolQueryBuilder.minimumShouldMatch("1");
        
        Query query = Query.of(q -> q.bool(boolQueryBuilder.build()));
        
        Pageable pageable = PageRequest.of(page - 1, size);
        
        return new NativeQueryBuilder()
                .withQuery(query)
                .withPageable(pageable)
                .build();
    }

    @Override
    public boolean indexContent(SearchDocument document) {
        try {
            log.info("索引内容: id={}, title={}", 
                    document.getId(), document.getTitle());
            
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
            log.info("更新索引: id={}", document.getId());
            
            searchDocumentRepository.save(document);
            return true;
        } catch (Exception e) {
            log.error("更新索引失败: {}", e.getMessage(), e);
            return false;
        }
    }

    // TODO: 实现逻辑删除
    @Override
    public boolean deleteIndex(Long id) {
        try {
            log.info("删除索引: id={}", id);
            searchDocumentRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            log.error("删除索引失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public SearchDocument getIndex(Long id) {
        try {
            return searchDocumentRepository.findById(id).orElse(null);
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
