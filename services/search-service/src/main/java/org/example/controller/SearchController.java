package org.example.controller;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.model.entity.SearchDocument;
import org.example.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 搜索内容
     * GET /api/search
     *
     * @param keyword 搜索关键词
     * @param type 搜索类型（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果
     */
    @GetMapping
    public Map<String, Object> search(
            @RequestParam String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("搜索关键词: {}, 类型: {}, 页码: {}, 每页大小: {}", keyword, type, page, size);
        
        return searchService.search(keyword, type, page, size);
    }

    /**
     * 索引内容
     * POST /api/search/index
     *
     * @param document 搜索文档
     * @return 索引结果
     */
    @PostMapping("/index")
    public Map<String, Object> indexContent(@RequestBody SearchDocument document) {
        log.info("索引内容: type={}, targetId={}, title={}", 
                document.getType(), document.getTargetId(), document.getTitle());
        
        boolean success = searchService.indexContent(document);
        return Map.of(
                "success", success,
                "message", success ? "索引成功" : "索引失败"
        );
    }

    /**
     * 更新索引
     * PUT /api/search/index
     *
     * @param document 搜索文档
     * @return 更新结果
     */
    @PutMapping("/index")
    public Map<String, Object> updateIndex(@RequestBody SearchDocument document) {
        log.info("更新索引: type={}, targetId={}", document.getType(), document.getTargetId());
        
        boolean success = searchService.updateIndex(document);
        return Map.of(
                "success", success,
                "message", success ? "更新成功" : "更新失败"
        );
    }

    /**
     * 删除索引
     * DELETE /api/search/index
     *
     * @param type 内容类型
     * @param id 内容ID
     * @return 删除结果
     */
    @DeleteMapping("/index")
    public Map<String, Object> deleteIndex(
            @RequestParam String type,
            @RequestParam Long id) {
        log.info("删除索引: type={}, id={}", type, id);
        
        boolean success = searchService.deleteIndex(type, id);
        return Map.of(
                "success", success,
                "message", success ? "删除成功" : "删除失败"
        );
    }

    /**
     * 获取索引
     * GET /api/search/index
     *
     * @param type 内容类型
     * @param id 内容ID
     * @return 索引文档
     */
    @GetMapping("/index")
    public Map<String, Object> getIndex(
            @RequestParam String type,
            @RequestParam Long id) {
        log.info("获取索引: type={}, id={}", type, id);
        
        SearchDocument document = searchService.getIndex(type, id);
        return Map.of(
                "success", document != null,
                "data", document
        );
    }

    /**
     * 内部接口：搜索内容（供其他服务 Feign 调用）
     * GET /api/search/internal/results
     *
     * @param keyword 搜索关键词
     * @param type 搜索类型（可选）
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果列表
     */
    @GetMapping("/internal/results")
    public ResponseEntity<List<Map<String, Object>>> searchForFeign(
            @RequestParam String keyword,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Map<String, Object>> results = searchService.searchForFeign(keyword, type, page, size);
        return ResponseEntity.ok(results);
    }

    /**
     * 健康检查接口
     * GET /api/search/health
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        return Map.of(
                "status", "UP",
                "service", "search-service",
                "message", "服务运行正常"
        );
    }
}
