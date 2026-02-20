package org.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "captcha-service")
public interface CaptchaFeignClient {

    @PostMapping("/api/email/template")
    String sendCaptchaCode(String email);
}
