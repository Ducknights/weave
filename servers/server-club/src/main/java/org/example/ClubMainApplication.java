package org.example;

import org.example.config.RedisConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class ClubMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClubMainApplication.class, args);
    }
}