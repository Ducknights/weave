package org.example.service;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.CacheKey;
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
import java.util.stream.Collectors;

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
        // 查找会话表
        List<Conversation> conversations = conversationMapper.findByUserId(userId);
        if (conversations == null || conversations.isEmpty()) {
            return new ArrayList<>();
        }
        // 获取到对方的id
        Set<Long> otherUserIds = conversations.stream()
                .map(conversation -> Objects.equals(conversation.getUserSmallId(), userId)
                        ? conversation.getUserBigId()
                        : conversation.getUserSmallId())
                .collect(Collectors.toSet());
        // 根据id获取到对方信息（用户名，头像，在线状态）
        Map<Long, User> userInfos = userInfoFeign.getUserInfosByIds(otherUserIds);
        // 构造返回体
        return conversations.stream().map(conversation -> {
            ConversationVo vo = new ConversationVo();
            // 会话id
            vo.setId(conversation.getId());
            // 用户id
            vo.setUserId(userId);
            // 对方id
            Long otherUserId = Objects.equals(conversation.getUserSmallId(), userId)
                    ? conversation.getUserBigId()
                    : conversation.getUserSmallId();
            vo.setOtherUserId(otherUserId);
            // 对方信息
            User user = userInfos.get(otherUserId);
            if (user != null) {
                vo.setOtherUserNickname(user.getName());
                vo.setOtherUserAvatar(user.getAvatar());
            }
            // 最后的消息
            vo.setLastMessage(conversation.getLastMessage());
            vo.setLastMessageTime(conversation.getLastMessageTime());
            // 是否在线
            vo.setOnline(redisTemplate.hasKey(CacheKey.buildCacheKey(CacheKey.USER_INFO_AREA,userId)));
            return vo;
        }).collect(Collectors.toList());
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
    public List<Message> getMessages(Long conversationId ,int page,int size) {
        return messageMapper.selectLastN(conversationId,page,size);
    }

}
