package org.example.auto;


import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = "common.auto", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({
        org.example.config.MybatisPlusConfig.class,
        org.example.config.RabbitMQConfig.class,
        org.example.config.RedisConfig.class
})
public class CommonAutoConfiguration {
}
