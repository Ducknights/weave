package org.example.auto;

import org.example.config.RedisConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = "redis.auto", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import(RedisConfig.class)
public class RedisAutoConfiguration {
}
