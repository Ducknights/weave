package com.weave.post.feign;

import com.weave.model.model.dto.ClubBriefDto;
import com.weave.post.feign.fallback.ClubFeignClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Set;

@FeignClient(name = "club-service", fallback = ClubFeignClientFallback.class)
public interface ClubFeignClient {

    @PostMapping("/api/club/batch")
    Map<Long, ClubBriefDto> getClubInfosByIds(@RequestBody Set<Long> clubIds);
}
