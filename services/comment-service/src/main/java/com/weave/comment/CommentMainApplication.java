package com.weave.comment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CommentMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(CommentMainApplication.class, args);
    }
}