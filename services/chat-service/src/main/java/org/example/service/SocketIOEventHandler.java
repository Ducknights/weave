package org.example.service;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.mapper.ConversationMemberMapper;
import org.springframework.data.redis.connection.RedisKeyCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.scheduling.annotation.Scheduled;
import org.example.constant.CacheKey;
import org.example.util.JwtUtil;
import org.example.mapper.ConversationMapper;
import org.example.mapper.MessageMapper;
import org.example.model.dto.SendMessageDTO;
import org.example.model.entity.Conversation;
import org.example.model.entity.Message;
import org.example.model.enums.MessageType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class SocketIOEventHandler {

    @Resource
    private SocketIOServer socketIOServer;
    @Resource
    private MessageService messageService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ConversationService conversationService;
    // 用户ID -> SocketIOClient 映射
    private final Map<Long, SocketIOClient> onlineClients = new ConcurrentHashMap<>();

    private final String USER_ID = "userId";
    private final String CHAT_SEND = "chat:send";
    private final String CHAT_RECEIVE = "chat:receive";


    @PostConstruct
    public void start() {
        socketIOServer.start();
        log.info("Socket.IO 运行在端口: {}", socketIOServer.getConfiguration().getPort());
    }

    @PreDestroy
    public void stop() {
        socketIOServer.stop();
    }

    /**
     * 客户端连接事件
     * 认证已由网关处理，从X-User-Id请求头获取userId
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        Long userId = getUserIdFromToken(client);
        client.set(USER_ID, userId);
        onlineClients.put(userId, client);
        redisTemplate.opsForValue().set(CacheKey.buildCacheKey(CacheKey.USER_ONLINE, userId), "true", 90, TimeUnit.SECONDS);
        log.info("用户上线: userId={}, sessionId={}", userId, client.getSessionId());
    }

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        Long userId = client.get(USER_ID);
        // 仅当断开的连接与当前在线连接一致时才清理，防止误删新连接
        onlineClients.remove(userId, client);
        // 如果用户还有其他连接则不删Redis在线状态
        if (!onlineClients.containsKey(userId)) {
            redisTemplate.delete(CacheKey.buildCacheKey(CacheKey.USER_ONLINE, userId));
        }
        log.info("用户下线: userId={}, sessionId={}", userId, client.getSessionId());
    }

    /**
     * 每30秒刷新所有在线用户的Redis TTL
     * pingInterval=25s，30s足够覆盖一轮心跳
     */
    @Scheduled(fixedRate = 30000)
    public void refreshOnlineStatus() {
        if (onlineClients.isEmpty()) {
            return;
        }
        log.info("刷新在线状态: {}", onlineClients.keySet());
        // 制作快照
        List<Long> userIds = List.copyOf(onlineClients.keySet());
        // 构造Redis键列表
        List<byte[]> keys = userIds
                .stream()
                .map(uid -> CacheKey.buildCacheKey(
                        CacheKey.USER_ONLINE, uid))
                .map(k -> k.getBytes(StandardCharsets.UTF_8))
                .toList();
        // 批量设置键的TTL
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            RedisKeyCommands keyCommands = connection.keyCommands();
            keys.forEach(key ->
                    keyCommands.pExpire(key, 90_000));
            return null;
        });
    }

    /**
     * 处理客户端通过 WebSocket发送的消息
     */
    @OnEvent(CHAT_SEND)
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = CacheKey.CONVERSATION, key = "#dto.fromUserId + ':' + #dto.toUserId"),
            @CacheEvict(value = CacheKey.CONVERSATION, key = "#dto.toUserId + ':' + #dto.fromUserId")
    })
    public void onSendMessage(SocketIOClient client, SendMessageDTO dto, AckRequest ackRequest) {
        Long fromId = client.get(USER_ID);  // 获取用户ID
        Long toId = dto.getToUserId();  // 获取接收者ID
        String content = dto.getContent();  // 获取消息内容
        // 保存消息
        Message message = messageService.saveMessage(fromId, toId, content);
        log.info("保存的消息: {}", message);
        try {
            // TODO: 使用 RabbitMQ进行异步推送
            pushMessage(toId, message);
            // 发送回执(消息发送成功)
            ackRequest.sendAckData(message);
        } catch (Exception e) {
            log.error("发送消息失败: {}", e.getMessage());
            ackRequest.sendAckData("发送消息失败");
        }
    }

    /**
     * 推送消息给指定用户
     */
    public void pushMessage(Long toqId, Message message) {
        SocketIOClient client = onlineClients.get(toqId);
        if (client != null && client.isChannelOpen()) {
            client.sendEvent(CHAT_RECEIVE, message);
            log.debug("推送消息给用户: userId={}, messageId={}", toqId, message.getId());
        }else {
            log.debug("用户不在线: userId={}", toqId);
        }
    }

    /**
     * 从握手URL参数获取 token，解析JWT得到 userId
     */
    private Long getUserIdFromToken(SocketIOClient client) {
        String token = client.getHandshakeData().getSingleUrlParam("token");
        String subject = JwtUtil.getJwtSubject(token);
        String userIdStr = subject.substring(subject.indexOf("::") + 2);
        return Long.valueOf(userIdStr);
    }
}
