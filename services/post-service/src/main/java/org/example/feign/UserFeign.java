package org.example.feign;

import org.example.dto.UserBriefDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Set;

@FeignClient(name = "user-service")
public interface UserFeign {

    @PostMapping("/api/user/batch")
    Map<Long, UserBriefDto> getUserInfosByIds(@RequestBody Set<Long> ids);
}
