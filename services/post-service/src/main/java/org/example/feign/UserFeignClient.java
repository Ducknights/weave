package org.example.feign;

import org.example.model.dto.UserBriefDto;
import org.example.feign.fallback.UserFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Set;

@FeignClient(name = "user-service", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {

    @PostMapping("/api/user/info/batch")
    Map<Long, UserBriefDto> getUserInfosByIds(@RequestBody Set<Long> ids);

    @GetMapping("/api/user/{id}/loadCache")
    void loadCacheLikeAndCollect(@PathVariable Long id);
}
