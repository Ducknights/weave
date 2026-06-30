package org.example.feign;

import org.example.feign.fallback.RecommendFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "recommend-service", fallback = RecommendFeignClientFallback.class)
public interface RecommendFeignClient {

    @GetMapping("/api/recommend/post")
    List<Long> getRecommendations(@RequestParam Long userId, @RequestParam int limit);
}
