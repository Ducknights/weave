package org.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.example.model.entity.ConversationUser;

import java.util.List;

@Mapper
public interface ConversationUserMapper extends BaseMapper<ConversationUser> {

    @Update("UPDATE conversation_user SET unread_count = unread_count + 1 WHERE conversation_id = #{conversationId} AND user_id = #{userId}")
    void incrementUnreadCount(Long conversationId, Long userId);

    @Update("UPDATE conversation_user SET unread_count = 0 WHERE conversation_id = #{conversationId} AND user_id = #{userId}")
    void resetUnreadCount(Long conversationId, Long userId);

    default List<ConversationUser> selectByConversationIdsAndUserId(List<Long> conversationIds, Long userId) {
        LambdaQueryWrapper<ConversationUser> queryWrapper = new LambdaQueryWrapper<ConversationUser>()
                .eq(ConversationUser::getUserId, userId)
                .in(ConversationUser::getConversationId, conversationIds);
        return this.selectList(queryWrapper);
    }
}
