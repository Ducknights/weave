package org.example.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.whitelist")
public class GatewayWhitelistProperties {
    private List<String> paths = new ArrayList<>();
}