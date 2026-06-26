package org.example.controller;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.dto.PostDetailVo;
import org.example.model.dto.SearchPageDto;
import org.example.model.dto.SearchResultDto;
import org.example.model.entity.SearchDocument;
import org.example.model.enums.SearchApiStatus;
import org.example.service.ConversionService;
import org.example.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 * 提供搜索相关的RESTful API接口
 */
@Log4j2
@RestController
@RequestMapping("/api/search")
public class SearchController {

    @Resource
    private SearchService searchService;

    @Resource
    private ConversionService conversionService;

    /**
     * 搜索内容
     * GET /api/search
     *
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @GetMapping("/post")
    public ResponseEntity<?> searchPost(@RequestParam String keyword,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        log.info("搜索关键词: {}, 页码: {}, 每页大小: {}", keyword, page, size);

        // 1. 从 ES 获取搜索结果（仅包含 ID 和分数）
        List<SearchResultDto> results = searchService.search(keyword, page, size);

        // 2. 通过 ConversionService 获取完整帖子信息并设置搜索分数
        List<PostDetailVo> postList = conversionService.convertToPostDetailVo(results);

        // 3. 组装分页结果
        SearchPageDto result = SearchPageDto.builder()
                .keyword(keyword)
                .posts(postList)
                .pageNum(page)
                .pageSize(size)
                .total(postList.size())
                .build();

        return ResponseEntity.ok(SearchApiStatus.SEARCH_SUCCESS.response(result));
    }

    /**
     * 索引内容
     * POST_HASH /api/search/index
     *
     * @param document 搜索文档
     * @return 索引结果
     */
    @PostMapping("/index")
    public ResponseEntity<?> indexContent(@RequestBody SearchDocument document) {
        log.info("索引内容: id={}, title={}", 
                document.getId(), document.getTitle());
        
        boolean success = searchService.indexContent(document);
        if (success) {
            return ResponseEntity.ok().body(SearchApiStatus.INDEX_SUCCESS.response());
        } else {
            return ResponseEntity.ok().body(SearchApiStatus.INDEX_FAILED.response());
        }
    }

    /**
     * 更新索引
     * PUT /api/search/index
     *
     * @param document 搜索文档
     * @return 更新结果
     */
    @PutMapping("/index")
    public ResponseEntity<?> updateIndex(@RequestBody SearchDocument document) {
        log.info("更新索引: id={}", document.getId());
        
        boolean success = searchService.updateIndex(document);
        if (success) {
            return ResponseEntity.ok().body(SearchApiStatus.UPDATE_INDEX_SUCCESS.response());
        } else {
            return ResponseEntity.ok().body(SearchApiStatus.UPDATE_INDEX_FAILED.response());
        }
    }

    /**
     * 删除索引
     * DELETE /api/search/index
     *
     * @param id 内容ID
     * @return 删除结果
     */
    @DeleteMapping("/index")
    public ResponseEntity<?> deleteIndex(@RequestParam Long id) {
        log.info("删除索引: id={}", id);
        
        boolean success = searchService.deleteIndex(id);
        if (success) {
            return ResponseEntity.ok().body(SearchApiStatus.DELETE_INDEX_SUCCESS.response());
        } else {
            return ResponseEntity.ok().body(SearchApiStatus.DELETE_INDEX_FAILED.response());
        }
    }

    /**
     * 获取索引
     * GET /api/search/index
     *
     * @param id 内容ID
     * @return 索引文档
     */
    @GetMapping("/index")
    public ResponseEntity<?> getIndex(@RequestParam Long id) {
        log.info("获取索引: id={}", id);
        
        SearchDocument document = searchService.getIndex(id);
        if (document != null) {
            return ResponseEntity.ok().body(SearchApiStatus.GET_INDEX_SUCCESS.response(document));
        } else {
            return ResponseEntity.ok().body(SearchApiStatus.DOCUMENT_NOT_FOUND.response());
        }
    }

    /**
     * 健康检查接口
     * GET /api/search/health
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("service", "search-service");
        data.put("message", "服务运行正常");
        return ResponseEntity.ok().body(SearchApiStatus.SUCCESS.response(data));
    }
}
