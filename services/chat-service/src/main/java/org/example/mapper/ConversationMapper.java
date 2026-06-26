package org.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.example.model.entity.Conversation;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {

    // 查找某个用户ID的全部会话
    default List<Conversation> findByUserId(Long userId){
        return selectList(new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getUserSmallId, userId)
                .or()
                .eq(Conversation::getUserBigId, userId));
    }

    // 创建初始会话
    default Long createConversation(Long userA, Long userB){
        Conversation conversation = Conversation.builder()
                .userSmallId(Math.min(userA, userB))
                .userBigId(Math.max(userA, userB))
                .createTime(LocalDateTime.now())
                .build();
        return insert(conversation) > 0 ? conversation.getId() : null;
    }

    // 根据双方用户ID获取私聊会话ID
    default Long getConversationIdByUsers(Long smallId, Long bigId) {
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getUserSmallId, smallId)
                .eq(Conversation::getUserBigId, bigId);
        Conversation conversation = selectOne(wrapper);
        return conversation != null ? conversation.getId() : null;
    }

    // 更新会话内容
    default void updateConversation(Long conversationId, String content){
        Conversation conversation = Conversation.builder()
                .id(conversationId)
                .lastMessage(content)
                .build();
        updateById(conversation);
    }
}

