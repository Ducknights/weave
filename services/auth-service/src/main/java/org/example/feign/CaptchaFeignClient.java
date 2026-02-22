package org.example.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "captcha-service")
public interface CaptchaFeignClient {

    /**
     * 发送验证码模板邮件的API接口
     *
     * @param email 接收验证码的邮箱地址
     * @return 操作结果信息
     */
    @PostMapping("/api/email/template")
    String sendCaptchaCode(String email);
}
