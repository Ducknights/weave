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
import org.example.constant.MQueue;
import org.example.model.dto.ConversationMemberParam;
import org.example.model.dto.PushMessageDto;
import org.example.util.MQUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.connection.RedisKeyCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.scheduling.annotation.Scheduled;
import org.example.constant.CacheKey;
import org.example.util.JwtUtil;
import org.example.model.dto.SendMessageDTO;
import org.example.model.entity.Message;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
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
    private ConversationMemberService conversationMemberService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private MQUtil mqUtil;
    // 用户ID -> SocketIOClient 映射
    private final Map<Long, SocketIOClient> onlineClients = new ConcurrentHashMap<>();

    private final String USER_ID = "userId";
    private final String MESSAGE_SEND = "message:send";
    private final String MESSAGE_PUSH = "message:push";
    private final String CONVERSATION_ENTER = "conversation:enter";
    private final String CONVERSATION_LEAVE = "conversation:leave";
    private final String CURRENT_CONVERSATION = "currentConversation";

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
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        Long userId = getUserIdFromToken(client);
        client.set(USER_ID, userId);
        onlineClients.put(userId, client);
        redisTemplate.opsForValue().set(CacheKey.buildCacheKey(CacheKey.USER_ONLINE, userId), "true", 90, TimeUnit.SECONDS);
        log.info("用户上线: userId={}, sessionId={}", userId, client.getSessionId());
    }

    /**
     * 客户端断开连接事件
     */
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
    @OnEvent(MESSAGE_SEND)
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
        // 发送回执(消息发送成功)
        ackRequest.sendAckData(message);
        PushMessageDto pushMessageDto = PushMessageDto.builder()
                .toId(toId)
                .message(message)
                .build();
        mqUtil.pushChatMessage(pushMessageDto);
    }

    /**
     * 推送消息给指定用户
     */
    @RabbitListener(queues = MQueue.CHAT_PUSH_QUEUE)
    public void pushMessage(PushMessageDto dto) {
        SocketIOClient client = onlineClients.get(dto.getToId());
        // 如果用户在线且连接正常则推送消息
        if (client != null && client.isChannelOpen()) {
            client.sendEvent(MESSAGE_PUSH, dto.getMessage());
            log.debug("推送消息给用户: userId={}, messageId={}", dto.getToId(), dto.getMessage().getId());
            // 如果用户在当前会话中，则更新会话成员的已读状态,和清除未读消息数
            if (client.get(CURRENT_CONVERSATION).equals(dto.getMessage().getConversationId())){
                ConversationMemberParam param = ConversationMemberParam.builder()
                        .userId(dto.getToId())
                        .conversationId(dto.getMessage().getConversationId())
                        .build();
                conversationMemberService.updateUserLastReadMessageId(param, dto.getMessage().getId());
                conversationMemberService.resetUnreadCount(param);
            }
        }else {
            log.debug("用户userId={} 不在线，等待客服端主动拉取", dto.getToId());
        }
    }

    /**
     * 处理客户端通过 WebSocket 发送的会话进入事件
     */
    @OnEvent(CONVERSATION_ENTER)
    public void onConversationEnter(SocketIOClient client, Long conversationId) {
        client.set(CURRENT_CONVERSATION, conversationId);
        ConversationMemberParam param = ConversationMemberParam.builder()
                .userId(client.get(USER_ID))
                .conversationId(conversationId)
                .build();
        // 获取该会话最新的消息ID
        Long newMessageId = messageService.getNewMessageId(conversationId);
        // 更新已读消息ID
        conversationMemberService.updateUserLastReadMessageId(param, newMessageId);
        // 重置未读消息数
        conversationMemberService.resetUnreadCount(param);
    }

    /**
     * 处理客户端通过 WebSocket 发送的会话离开事件
     */
    @OnEvent(CONVERSATION_LEAVE)
    public void onConversationLeave(SocketIOClient client, Long conversationId) {
        client.set(CURRENT_CONVERSATION, 0L);
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
