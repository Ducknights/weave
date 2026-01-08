package org.example;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ResourcesMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ResourcesMainApplication.class, args);
    }
}