package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication()
public class CaptchaMainApplication {
    public static void main(String[] args) {
        // 不启动Web容器，只启动消息监听
        new SpringApplicationBuilder(CaptchaMainApplication.class)
                .web(WebApplicationType.NONE)  // 关键：不启动Web服务器
                .run(args);
    }
}
