package org.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.example.model.entity.Message;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    default List<Message> selectLastN(Long userId, Long conversationId, int page, int size) {
        Page<Message> messagePage = new Page<>(page, size);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>(Message.class)
                .eq(Message::getFromUserId, userId).or().eq(Message::getToUserId, userId)
                .eq(Message::getConversationId, conversationId)
                .orderByDesc(Message::getCreateTime);
        Page<Message> resultPage = selectPage(messagePage, wrapper);
        return resultPage.getRecords();
    }

    default List<Message> selectNewMessages(Long userId, Long conversationId, Long lastReadMessageId){
        return selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getFromUserId, userId).or().eq(Message::getToUserId, userId)
                .eq(Message::getConversationId, conversationId)
                .gt(Message::getId, lastReadMessageId)
                .orderByDesc(Message::getCreateTime));
    }
}
