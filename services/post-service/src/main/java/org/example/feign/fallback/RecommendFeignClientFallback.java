package org.example.feign.fallback;

import lombok.extern.log4j.Log4j2;
import org.example.feign.RecommendFeignClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Log4j2
@Component
public class RecommendFeignClientFallback implements RecommendFeignClient {

    @Override
    public List<Long> getRecommendations(int limit) {
        log.warn("RecommendFeignClient.getRecommendations 降级: limit={}", limit);
        return Collections.emptyList();
    }
}
