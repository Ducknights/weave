package com.weave.rabbitmq.auto;

import com.weave.rabbitmq.config.RabbitMQConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = "rabbitmq.auto", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import(RabbitMQConfig.class)
public class RabbitMQAutoConfiguration {
}
