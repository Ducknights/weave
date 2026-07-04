package com.weave.minio.auto;

import com.weave.minio.config.MinioConfig;
import com.weave.minio.service.FileService;
import com.weave.minio.service.impl.FileServiceImpl;
import com.weave.minio.util.MinioUtil;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({MinioClient.class, MinioConfig.class})
@EnableConfigurationProperties(MinioConfig.class)
public class MinioAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MinioClient minioClient(MinioConfig minioConfig) {
        return MinioClient.builder()
                .endpoint(minioConfig.getEndpoint())
                .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey())
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public MinioUtil minioUtil(MinioClient minioClient, MinioConfig minioConfig) {
        return new MinioUtil(minioClient, minioConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "minio.file-service", name = "enabled", havingValue = "true", matchIfMissing = true)
    public FileService fileService(MinioUtil minioUtil) {
        return new FileServiceImpl(minioUtil);
    }
}
