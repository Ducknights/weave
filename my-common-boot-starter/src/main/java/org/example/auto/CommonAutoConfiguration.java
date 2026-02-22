package org.example.auto;

import org.example.config.MybatisPlusConfig;
import org.example.config.RabbitMQConfig;
import org.example.config.RedisConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = "common.auto", name = "enabled", havingValue = "true",matchIfMissing = true)
@Import({
        MybatisPlusConfig.class,
        RabbitMQConfig.class,
        RedisConfig.class
        // 添加其他配置类
})
public class CommonAutoConfiguration {
}
