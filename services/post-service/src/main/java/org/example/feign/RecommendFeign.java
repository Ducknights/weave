package org.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "recommend-service")
public interface RecommendFeign {

    @GetMapping("/api/recommend")
    List<Long> getRecommendations(@RequestParam(defaultValue = "10") int limit);
}
