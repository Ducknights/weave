package com.weave.post.feign.fallback;

import com.weave.post.feign.ClubFeignClient;
import lombok.extern.log4j.Log4j2;
import com.weave.model.model.dto.ClubBriefDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Log4j2
@Component
public class ClubFeignClientFallback implements ClubFeignClient {

    @Override
    public Map<Long, ClubBriefDto> getClubInfosByIds(Set<Long> clubIds) {
        log.warn("ClubFeignClient.getClubInfosByIds 降级: ids.size={}", clubIds != null ? clubIds.size() : 0);
        return Collections.emptyMap();
    }
}
