package org.example.consumer;

import lombok.extern.log4j.Log4j2;
import org.example.constant.MQueue;
import org.example.constant.PostOperation;
import org.example.model.PostActionMessage;
import org.example.model.dto.ActionDto;
import org.example.model.eunms.ActionEnum;
import org.example.service.ActionService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@Log4j2
public class PostActionMessageConsumer {

    private final ActionService actionService;

    public PostActionMessageConsumer(ActionService actionService) {
        this.actionService = actionService;
    }

    /** 操作类型 → ActionEnum 映射（执行添加） */
    private static final Map<String, ActionEnum> ACTION_TYPE_MAP = Map.of(
            PostOperation.LIKE, ActionEnum.LIKE,
            PostOperation.COLLECT, ActionEnum.COLLECT,
            PostOperation.VIEW, ActionEnum.VIEW
    );

    /** 需要执行删除的操作 */
    private static final Set<String> DELETE_ACTIONS = Set.of(
            PostOperation.UNLIKE,
            PostOperation.UNCOLLECT,
            PostOperation.DELETE_VIEW
    );

    /** 监听帖子操作队列 */
    @RabbitListener(queues = MQueue.POST_ACTION_QUEUE)
    public void handlePostAction(PostActionMessage message) {
        String action = message.getAction();
        ActionEnum type = ACTION_TYPE_MAP.get(action);

        if (type == null && !DELETE_ACTIONS.contains(action)) {
            log.warn("未知的操作类型: {}", action);
            return;
        }

        ActionDto actionDto = ActionDto.builder()
                .userId(message.getUserId())
                .targetId(message.getPostId())
                .type(type)
                .build();

        if (type != null) {
            actionService.addRecord(actionDto);
        } else {
            actionService.deleteRecord(actionDto);
        }
    }
}