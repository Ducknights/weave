package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UserMainApplication {
    public static void main(String[] args) {
         SpringApplication.run(UserMainApplication.class, args);
    }
}