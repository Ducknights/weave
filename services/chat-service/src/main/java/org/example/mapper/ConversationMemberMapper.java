package org.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.example.model.entity.ConversationMember;

import java.util.List;

@Mapper
public interface ConversationMemberMapper extends BaseMapper<ConversationMember> {

    // 更新用户会话的未读消息计数
    @Update("UPDATE conversation_member SET unread_count = unread_count + 1 WHERE conversation_id = #{conversationId} AND user_id = #{userId}")
    void incrementUnreadCount(Long conversationId, Long userId);

    // 重置用户会话的未读消息计数
    @Update("UPDATE conversation_member SET unread_count = 0 WHERE conversation_id = #{conversationId} AND user_id = #{userId}")
    void resetUnreadCount(Long conversationId, Long userId);

    // 根据会话ID和用户ID查询会话成员
    default List<ConversationMember> selectByConversationIdsAndUserId(List<Long> conversationIds, Long userId) {
        LambdaQueryWrapper<ConversationMember> queryWrapper = new LambdaQueryWrapper<ConversationMember>()
                .eq(ConversationMember::getUserId, userId)
                .in(ConversationMember::getConversationId, conversationIds);
        return this.selectList(queryWrapper);
    }

    // 添加会话成员
    default void addConversationMember(Long conversationId, Long userId){
        ConversationMember conversationMember = ConversationMember.builder()
                .conversationId(conversationId)
                .userId(userId)
                .build();
        this.insert(conversationMember);
    }

    // 根据用户ID和会话ID获取最后阅读消息ID
    default Long getLastReadMessageId(Long userId, Long conversationId){
        return this.selectOne(new LambdaQueryWrapper<ConversationMember>()
                .eq(ConversationMember::getUserId, userId)
                .eq(ConversationMember::getConversationId, conversationId)).getLastReadMessageId();
    }

    // 更新用户会话的最后阅读消息ID
    default void updateUserLastReadMessageId(Long conversationId, Long userId, Long messageId){
        this.update(ConversationMember.builder()
                .conversationId(conversationId)
                .userId(userId)
                .lastReadMessageId(messageId)
                .build(), new LambdaQueryWrapper<ConversationMember>()
                .eq(ConversationMember::getConversationId, conversationId)
                .eq(ConversationMember::getUserId, userId));
    }
}
