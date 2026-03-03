package org.example.auto;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "permission.auto", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PermissionAutoConfiguration {
}
