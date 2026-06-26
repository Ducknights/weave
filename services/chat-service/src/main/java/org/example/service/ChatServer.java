package org.example.service;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.CacheKey;
import org.example.dto.UserBriefDto;
import org.example.feign.UserInfoFeign;
import org.example.mapper.ConversationMapper;
import org.example.mapper.ConversationUserMapper;
import org.example.mapper.MessageMapper;
import org.example.model.entity.Conversation;
import org.example.model.entity.ConversationUser;
import org.example.model.entity.Message;
import org.example.model.vo.ConversationVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
public class ChatServer {
    @Resource
    private MessageMapper messageMapper;
    @Resource
    private ConversationMapper conversationMapper;
    @Resource
    private ConversationUserMapper conversationUserMapper;
    @Resource
    private UserInfoFeign userInfoFeign;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取会话列表
     */
    public List<ConversationVo> getConversations(Long userId) {
        // 查找用户会话
        List<Conversation> conversations = conversationMapper.findByUserId(userId);
        if (conversations == null || conversations.isEmpty()) {
            return Collections.emptyList();
        }
        return convertToConversationVo(userId, conversations);
    }

    /**
     * 获取消息
     */
    public List<Message> getMessages(Long userId, Long conversationId, int page, int size) {
        return messageMapper.selectLastN(userId, conversationId, page, size);
    }

    private List<ConversationVo> convertToConversationVo(Long userId, List<Conversation> conversations) {
        // 获取会话ID列表
        List<Long> conversationIds = conversations.stream().map(Conversation::getId).toList();

        // 查询会话用户信息
        Map<Long, ConversationUser> cuMap = conversationUserMapper
                .selectByConversationIdsAndUserId(conversationIds, userId)
                .stream()
                .collect(Collectors.toMap(ConversationUser::getConversationId, cu -> cu));

        // 批量获取对方用户信息
        Set<Long> otherUserIds = conversations.stream()
                .map(c -> c.getUserSmallId().equals(userId) ? c.getUserBigId() : c.getUserSmallId())
                .collect(Collectors.toSet());
        Map<Long, UserBriefDto> userInfos = userInfoFeign.getUserInfosByIds(otherUserIds);

        // 组装VO
        return conversations.stream().map(c -> buildVo(userId, c, cuMap, userInfos)).toList();
    }

    private ConversationVo buildVo(Long userId, Conversation conversation,
                                   Map<Long, ConversationUser> cuMap,
                                   Map<Long, UserBriefDto> userInfos) {
        // 获取对方用户ID
        Long otherUserId = conversation.getUserSmallId().equals(userId) ? conversation.getUserBigId() : conversation.getUserSmallId();
        UserBriefDto userDto = userInfos.get(otherUserId);
        // 获取会话用户信息
        ConversationUser cu = cuMap.get(conversation.getId());
        // 构建VO对象
        return ConversationVo.builder()
                .id(conversation.getId())
                .userId(userId)
                .otherUserId(otherUserId)
                .lastMessage(cu.getLastMessage())
                .lastMessageTime(cu.getLastMessageTime())
                .unreadMessageCount(cu.getUnreadCount())
                .otherUserNickname(userDto.getName())
                .otherUserAvatar(userDto.getAvatar())
                .online(redisTemplate.hasKey(CacheKey.buildCacheKey(CacheKey.USER_ONLINE, otherUserId)))
                .build();
    }
}
