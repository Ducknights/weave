package org.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserAvatarFeign {

    @GetMapping("/api/files/internal/presign")
    String getFileUrl(
            @RequestParam String filePath,
            @RequestParam(defaultValue = "3600") int expiry);
}
