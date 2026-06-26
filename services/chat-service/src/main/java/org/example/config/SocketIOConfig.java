package org.example.config;

import com.corundumstudio.socketio.AuthorizationResult;
import com.corundumstudio.socketio.SocketConfig;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import lombok.extern.log4j.Log4j2;
import org.example.constant.CacheKey;
import org.example.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@Log4j2
@Configuration
@EnableScheduling
public class SocketIOConfig {

    @Value("${socketio.host:0.0.0.0}")
    private String host;
    @Value("${socketio.port:4301}")
    private int port;

    private final RedisTemplate<String, Object> redisTemplate;
    public SocketIOConfig(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);

        SocketConfig socketConfig = new SocketConfig();
        socketConfig.setReuseAddress(true);
        config.setSocketConfig(socketConfig);

        config.setOrigin("*");
        config.setPingInterval(25000);
        config.setPingTimeout(60000);

        config.setAuthorizationListener(data -> {
            String token = data.getSingleUrlParam("token");
            if (token == null || token.isEmpty()) {
                return AuthorizationResult.FAILED_AUTHORIZATION;
            }
            try {
                String subject = JwtUtil.getJwtSubject(token);
                String userIdStr = subject.substring(subject.indexOf("::") + 2);
                Long userId = Long.valueOf(userIdStr);
                String key = CacheKey.buildCacheKey(CacheKey.USER_AUTHORITY, userId);
                if (Boolean.FALSE.equals(redisTemplate.hasKey(key))) {
                    return AuthorizationResult.FAILED_AUTHORIZATION;
                }
                return AuthorizationResult.SUCCESSFUL_AUTHORIZATION;
            } catch (Exception e) {
                return AuthorizationResult.FAILED_AUTHORIZATION;
            }
        });

        return new SocketIOServer(config);
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner(SocketIOServer socketIOServer) {
        return new SpringAnnotationScanner(socketIOServer);
    }
}
