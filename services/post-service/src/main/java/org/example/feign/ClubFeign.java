package org.example.feign;

import org.example.dto.ClubBriefDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Map;
import java.util.Set;

@FeignClient(name = "club-service")
public interface ClubFeign {

    @PostMapping("/api/club/batch")
    Map<Long, ClubBriefDto> getClubInfosByIds(Set<Long> clubIds);
}
