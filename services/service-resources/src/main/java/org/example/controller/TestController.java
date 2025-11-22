package org.example.controller;

import org.example.model.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 测试接口
     */
    @GetMapping("/health")
    public Result<String> healthCheck() {
        return Result.success("Service resources is running normally");
    }
}