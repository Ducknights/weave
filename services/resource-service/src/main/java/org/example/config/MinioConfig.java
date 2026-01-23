package org.example.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 创建 Minio 配置类
@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioConfig {
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String bucket;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}

