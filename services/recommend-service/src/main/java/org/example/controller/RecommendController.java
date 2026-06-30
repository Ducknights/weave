package org.example.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.service.RecommendService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/recommend")
public class RecommendController {

    @Resource
    private RecommendService recommendService;

    @GetMapping("/post")
    public List<Long> getRecommendations(@RequestParam(required = false) Long userId, @RequestParam int limit) {
        log.info("用户 {} 请求推荐，限制数量: {}", userId, limit);
        return recommendService.recommend(userId, limit);
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok().body("服务运行正常");
    }
}
