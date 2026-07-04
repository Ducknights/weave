package com.weave.club;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ClubMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClubMainApplication.class, args);
    }
}