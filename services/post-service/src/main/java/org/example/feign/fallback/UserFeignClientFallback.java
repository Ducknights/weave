package org.example.feign.fallback;

import lombok.extern.log4j.Log4j2;
import org.example.model.dto.UserBriefDto;
import org.example.feign.UserFeignClient;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

@Log4j2
@Component
public class UserFeignClientFallback implements UserFeignClient {

    @Override
    public Map<Long, UserBriefDto> getUserInfosByIds(Set<Long> ids) {
        log.warn("UserFeignClient.getUserInfosByIds 降级: ids.size={}", ids != null ? ids.size() : 0);
        return Collections.emptyMap();
    }

    @Override
    public void loadCacheLikeAndCollect(Long currentUserId) {
        log.warn("UserFeignClient.loadCacheLikeAndCollect 降级: userId={}", currentUserId);
    }
}
