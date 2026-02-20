package org.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.model.entity.Conversation;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {

    // 根据两个用户ID查找会话
    default Conversation findByUsers(Long userA, Long userB) {
        long small = Math.min(userA, userB);
        long big = Math.max(userA, userB);
        LambdaQueryWrapper<Conversation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Conversation::getUserSmallId, small)
                .eq(Conversation::getUserBigId, big);
        return selectOne(wrapper);
    }

    // 查找某个用户ID的全部会话
    default List<Conversation> findByUserId(Long userId){
        return selectList(new LambdaQueryWrapper<Conversation>()
                .eq(Conversation::getUserSmallId, userId)
                .or()
                .eq(Conversation::getUserBigId, userId));
    }

    // 创建初始会话
    default Conversation createConversation(Long userA, Long userB){
        Conversation conversation = Conversation.builder()
                .userSmallId(Math.min(userA, userB))
                .userBigId(Math.max(userA, userB))
                .createTime(LocalDateTime.now())
                .build();
        return insert(conversation) > 0 ? conversation : null;
    }
}
