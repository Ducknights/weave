package org.example.consumer;

import lombok.extern.slf4j.Slf4j;
import org.example.constant.MQueue;
import org.example.constant.PostOperation;
import org.example.model.dto.SearchDocumentDto;
import org.example.model.PostSyncMessage;
import org.example.model.entity.SearchDocument;
import org.example.service.SearchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PostSyncConsumer {

    private final SearchService searchService;

    public PostSyncConsumer(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * 监听审核队列，同步数据到 ES
     *
     * @param message 消息对象
     */
    @RabbitListener(queues = MQueue.POST_SYNC_QUEUE)
    public void handleSyncToES(PostSyncMessage message) {
        try {
            log.info("接收到同步到 ES 的消息: {}", message);
            // 获取消息中的数据和操作类型
            SearchDocumentDto data = message.getData();
            String operation = message.getOperation();
            // 转换为 SearchDocument 对象
            SearchDocument document = toSearchDocument(data);

            switch (operation) {
                case PostOperation.CREATE -> searchService.indexContent(document);
                case PostOperation.UPDATE -> searchService.updateIndex(document);
                case PostOperation.DELETE -> searchService.deleteIndex(document.getId());
            }
        } catch (Exception e) {
            log.error("处理同步到 ES 消息失败", e);
        }
    }

    private SearchDocument toSearchDocument(SearchDocumentDto dto) {
        return SearchDocument.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .content(dto.getContent())
                .isPublic(dto.getIsPublic())
                .build();
    }
}
