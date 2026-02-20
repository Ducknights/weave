package org.example.service;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.bean.RequestContext;
import org.example.feign.UserInfoFeign;
import org.example.mapper.ConversationMapper;
import org.example.mapper.ConversationUserMapper;
import org.example.mapper.MessageMapper;
import org.example.model.entity.Conversation;
import org.example.model.entity.ConversationUser;
import org.example.model.entity.Message;
import org.example.model.entity.User;
import org.example.model.vo.ConversationVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class ChatServer {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private ConversationMapper conversationMapper;
    @Resource
    private ConversationUserMapper conversationUserMapper;
    @Resource
    private LongPollingService longPollingService;
    @Resource
    private UserInfoFeign userInfoFeign;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 发送消息方法
     * @param fromId 发送者ID
     * @param toId 接收者ID
     * @param content 消息内容
     * @return 消息
     */
    @Transactional
    public Message sendMessage(Long fromId, Long toId, String content) {
        // 获取会话
        Conversation conversation = conversationMapper.findByUsers(fromId, toId);
        // 如果会话不存在则创建
        if (conversation==null){
            conversation = createConversation(fromId, toId);
        }
        // 保存消息
        Message message = Message.builder()
                .conversationId(conversation.getId())
                .fromUserId(fromId)
                .toUserId(toId)
                .content(content)
                .msgType(0)
                .createTime(LocalDateTime.now())
                .build();
        messageMapper.insert(message);
        // 更新会话
        conversationMapper.updateById(conversation);
        
        // 通知长轮询有新消息，唤醒接收者的轮询请求
        longPollingService.notifyNewMessage(toId);
        
        return message;
    }

    /**
     * 获取会话列表
     * @param userId 用户ID
     * @return 会话列表
     */
    public List<ConversationVo> getConversations(Long userId) {
        // 根据用户ID获取会话列表
        List<Conversation> conversations = conversationMapper.findByUserId(userId);
        if (conversations == null || conversations.isEmpty()) {
            return new ArrayList<>();
        }

        List<ConversationVo> conversationVos = new ArrayList<>(conversations.size());
        Set<Long> userIdsToQuery = new HashSet<>();

        // 第一遍：处理缓存中存在的用户信息
        conversations.forEach(conversation -> {
            ConversationVo conversationVo = new ConversationVo();
            conversationVo.setId(conversation.getId());

            Long otherUserId = Objects.equals(conversation.getUserSmallId(), userId)
                    ? conversation.getUserBigId()
                    : conversation.getUserSmallId();

            // 尝试从缓存获取
            String cacheKey = "user:" + otherUserId;
            User cachedUser = (User) redisTemplate.opsForValue().get(cacheKey);

            if (cachedUser != null) {
                conversationVo.setOtherUserNickname(cachedUser.getName());
                conversationVo.setOtherUserAvatar(cachedUser.getAvatar());
            } else {
                userIdsToQuery.add(otherUserId);
            }

            conversationVo.setLastMessage(conversation.getLastMessage());
            conversationVo.setLastMessageTime(conversation.getLastMessageTime());
            conversationVos.add(conversationVo);
        });

        // 批量查询未缓存的用户信息
        if (!userIdsToQuery.isEmpty()) {
            try {
                Map<Long, User> userInfos = userInfoFeign.getUserInfosByIds(userIdsToQuery);

                // 更新缓存和VO对象
                conversationVos.forEach(vo -> {
                    Long otherUserId = vo.getOtherUserId();

                    User user = userInfos.get(otherUserId);
                    if (user != null) {
                        vo.setOtherUserNickname(user.getName());
                        vo.setOtherUserAvatar(user.getAvatar());
                    }
                });
            } catch (Exception e) {
                log.error("获取用户信息失败", e);
                // 可以选择抛出异常或使用默认值
                throw new RuntimeException("获取用户信息失败", e);
            }
        }

        return conversationVos;
    }
    /**
     * 创建会话
     * @param userA 用户A
     * @param userB 用户B
     * @return 会话
     */
    public Conversation createConversation(Long userA, Long userB) {
        return conversationMapper.createConversation(userA, userB);
    }

    /**
     * 获取消息
     * @param conversationId 会话ID
     * @return 消息列表
     */
//    @CacheEvict(
//            value = "messages",
//            key ="'messages:' + #conversationId")
    public List<Message> getMessages(Long conversationId ,int page,int size) {
        return messageMapper.selectLastN(conversationId,page,size);
    }

}
