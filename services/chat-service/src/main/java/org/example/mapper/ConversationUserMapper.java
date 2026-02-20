package org.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.model.entity.Conversation;
import org.example.model.entity.ConversationUser;

import java.util.List;

@Mapper
public interface ConversationUserMapper extends BaseMapper<ConversationUser> {
    // 查找某个用户ID的全部会话
    default List<ConversationUser> findByUserId(Long userId){
        return selectList(new LambdaQueryWrapper<ConversationUser>()
                .eq(ConversationUser::getUserId, userId));
    }
}
