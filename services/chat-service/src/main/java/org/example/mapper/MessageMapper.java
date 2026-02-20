package org.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.example.bean.RequestContext;
import org.example.model.entity.Conversation;
import org.example.model.entity.Message;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
    default List<Message> selectLastN(Long conversationId, int page, int size) {
        Page<Message> messagePage = new Page<>(page, size);
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getConversationId, conversationId)
                .orderByDesc(Message::getCreateTime);
        Page<Message> resultPage = selectPage(messagePage, wrapper);
        return resultPage.getRecords();
    }

    default Message selectLatestMessage(Long conversationId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Message::getConversationId, conversationId)
                .orderByDesc(Message::getCreateTime)
                .last("LIMIT 1");
        return selectOne(wrapper);
    }

    default Message selectLatestMessageForUser(Long userId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getFromUserId, userId).or().eq(Message::getToUserId, userId))
                .orderByDesc(Message::getCreateTime)
                .last("LIMIT 1");
        return selectOne(wrapper);
    }

    default List<Message> selectNewMessages(Long userId, Long lastReceivedId) {
        LambdaQueryWrapper<Message> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Message::getFromUserId, userId).or().eq(Message::getToUserId, userId))
                .gt(Message::getId, lastReceivedId)
                .orderByAsc(Message::getCreateTime);
        return selectList(wrapper);
    }
}
