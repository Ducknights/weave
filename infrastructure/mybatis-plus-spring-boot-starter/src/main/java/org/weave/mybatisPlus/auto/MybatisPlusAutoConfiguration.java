package org.weave.mybatisPlus.auto;

import org.weave.mybatisPlus.config.MybatisPlusConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = "mybatis-plus.auto", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import(MybatisPlusConfig.class)
public class MybatisPlusAutoConfiguration {
}
