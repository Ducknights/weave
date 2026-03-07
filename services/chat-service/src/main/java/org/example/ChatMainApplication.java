package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableCaching
@EnableFeignClients
public class ChatMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatMainApplication.class, args);
    }
}