package com.weave.user.consumer;

import com.weave.user.model.dto.ActionDto;
import lombok.extern.log4j.Log4j2;
import com.weave.redis.constant.CacheKey;
import com.weave.rabbitmq.constant.MQueue;
import com.weave.model.constant.PostOperation;
import com.weave.model.model.PostActionMessage;
import com.weave.user.model.eunms.ActionEnum;
import com.weave.user.service.ActionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@Log4j2
public class PostActionMessageConsumer {

    private final ActionService actionService;
    private final RedisTemplate<String, Object> redisTemplate;

    public PostActionMessageConsumer(ActionService actionService,
                                     RedisTemplate<String, Object> redisTemplate) {
        this.actionService = actionService;
        this.redisTemplate = redisTemplate;
    }

    /** 操作类型 → ActionEnum + 缓存Key 联合映射 */
    private static final Map<String, ActionEnum> ACTION_TYPE_MAP = Map.of(
            PostOperation.LIKE, ActionEnum.LIKE,
            PostOperation.COLLECT, ActionEnum.COLLECT,
            PostOperation.VIEW, ActionEnum.VIEW,
            PostOperation.UNLIKE, ActionEnum.LIKE,
            PostOperation.UNCOLLECT, ActionEnum.COLLECT,
            PostOperation.DELETE_VIEW, ActionEnum.VIEW
    );

    /** 需要执行删除的操作 */
    private static final Set<String> DELETE_ACTIONS = Set.of(
            PostOperation.UNLIKE,
            PostOperation.UNCOLLECT,
            PostOperation.DELETE_VIEW
    );

    /** 监听帖子操作队列 */
    @RabbitListener(queues = MQueue.POST_ACTION_QUEUE_3)
    public void handlePostAction(PostActionMessage message) {
        log.info("接收到消息: {}", message);
        String action = message.getAction();
        ActionEnum type = ACTION_TYPE_MAP.get(action);

        if (type == null) {
            log.warn("未知的操作类型: {}", action);
            return;
        }

        ActionDto actionDto = ActionDto.builder()
                .userId(message.getUserId())
                .targetId(message.getPostId())
                .type(type)
                .build();

        String cacheKey = buildCacheKey(type, actionDto);

        if (!DELETE_ACTIONS.contains(action)) {
            actionService.addRecord(actionDto);
            try {
                redisTemplate.opsForSet().add(cacheKey, actionDto.targetId());
            } catch (Exception e) {
                log.error("缓存用户操作失败，用户ID: {}, 目标ID: {}，操作类型: {}", actionDto.userId(), actionDto.targetId(), actionDto.type(), e);
            }
        } else {
            actionService.deleteRecord(actionDto);
            try {
                redisTemplate.opsForSet().remove(cacheKey, actionDto.targetId());
            } catch (Exception e) {
                log.error("缓存用户操作失败，用户ID: {}, 目标ID: {}，操作类型: {}", actionDto.userId(), actionDto.targetId(), actionDto.type(), e);
            }
        }
    }

    private String buildCacheKey(ActionEnum type, ActionDto dto) {
        return switch (type) {
            case LIKE -> CacheKey.buildCacheKey(CacheKey.USER_LIKED_POSTS, dto.userId());
            case COLLECT -> CacheKey.buildCacheKey(CacheKey.USER_COLLECTED_POSTS, dto.userId());
            case VIEW -> CacheKey.buildCacheKey(CacheKey.USER_VIEWED_POSTS, dto.userId());
        };
    }
}