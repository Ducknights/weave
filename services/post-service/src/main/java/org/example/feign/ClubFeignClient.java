package org.example.feign;

import org.example.model.dto.ClubBriefDto;
import org.example.feign.fallback.ClubFeignClientFallback;
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
