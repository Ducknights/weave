package org.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.example.model.dto.ConversationMemberParam;
import org.example.model.entity.ConversationMember;

import java.util.List;

@Mapper
public interface ConversationMemberMapper extends BaseMapper<ConversationMember> {

    @Update("UPDATE conversation_member SET unread_count = unread_count + 1 WHERE conversation_id = #{param.conversationId} AND user_id = #{param.userId}")
    void incrementUnreadCount(ConversationMemberParam param);

    @Update("UPDATE conversation_member SET unread_count = 0 WHERE conversation_id = #{param.conversationId} AND user_id = #{param.userId}")
    void resetUnreadCount(ConversationMemberParam param);

    default List<ConversationMember> selectByConversationIdsAndUserId(List<Long> conversationIds, Long userId) {
        LambdaQueryWrapper<ConversationMember> queryWrapper = new LambdaQueryWrapper<ConversationMember>()
                .eq(ConversationMember::getUserId, userId)
                .in(ConversationMember::getConversationId, conversationIds);
        return this.selectList(queryWrapper);
    }

    default void addConversationMember(ConversationMemberParam param) {
        ConversationMember member = ConversationMember.builder()
                .conversationId(param.getConversationId())
                .userId(param.getUserId())
                .build();
        this.insert(member);
    }

    default Long getLastReadMessageId(ConversationMemberParam param) {
        return this.selectOne(new LambdaQueryWrapper<ConversationMember>()
                .eq(ConversationMember::getUserId, param.getUserId())
                .eq(ConversationMember::getConversationId, param.getConversationId()))
                .getLastReadMessageId();
    }

    default void updateUserLastReadMessageId(ConversationMemberParam param, Long messageId) {
        this.update(ConversationMember.builder()
                .conversationId(param.getConversationId())
                .userId(param.getUserId())
                .lastReadMessageId(messageId)
                .build(), new LambdaQueryWrapper<ConversationMember>()
                .eq(ConversationMember::getConversationId, param.getConversationId())
                .eq(ConversationMember::getUserId, param.getUserId()));
    }
}
