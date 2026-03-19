package org.example.feign;

import org.example.model.ApiResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "resource-service")
public interface UserAvatarFeign {

    @GetMapping("/internal/resources/url/{*filePath}")
    String getFileUrl(
            @PathVariable("filePath") String filePath,
            @RequestParam(defaultValue = "3600") int expiry);
}
