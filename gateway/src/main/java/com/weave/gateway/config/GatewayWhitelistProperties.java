package com.weave.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.whitelist")
public class GatewayWhitelistProperties {
    private List<String> paths = new ArrayList<>();

    /**
     * 解析白名单配置，返回 (HttpMethod, pathPattern) 列表。
     * 支持格式：
     * - "GET:/api/user/**"
     * - "POST_HASH:/api/auth/login"
     * - "/api/search/**"（默认允许所有方法）
     */
    public List<WhitelistEntry> parseEntries() {
        List<WhitelistEntry> entries = new ArrayList<>();
        for (String path : paths) {
            String trimmed = path.trim();
            if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                continue;
            }
            int colonIndex = trimmed.indexOf(':');
            if (colonIndex > 0) {
                String method = trimmed.substring(0, colonIndex).trim().toUpperCase();
                String pattern = trimmed.substring(colonIndex + 1).trim();
                entries.add(new WhitelistEntry(HttpMethod.valueOf(method), pattern));
            } else {
                entries.add(new WhitelistEntry(null, trimmed));
            }
        }
        return entries;
    }
        public record WhitelistEntry(HttpMethod method, String pathPattern) {
    }
}
