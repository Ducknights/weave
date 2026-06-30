package org.example.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.UserActionMapper;
import org.example.model.dto.ActionDto;
import org.example.model.entity.UserAction;
import org.example.service.ActionService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ActionServiceImpl implements ActionService {

    @Resource
    private UserActionMapper userActionMapper;

    @Override
    public void addRecord(ActionDto dto) {
        UserAction action =UserAction.builder()
                .userId(dto.userId())
                .postId(dto.postId())
                .type(dto.type())
                .createdTime(LocalDateTime.now())
                .build();
        try{
            userActionMapper.insert(action);
        } catch (Exception e) {
            log.error("添加用户行为记录失败: userId={}, postId={}, type={}", dto.userId(), dto.postId(), dto.type(), e);
        }
    }

    @Override
    public void deleteRecord(ActionDto dto) {
        userActionMapper.deleteByUserIdAndPostId(dto.userId(), dto.postId());
        log.debug("删除用户行为记录: userId={}, postId={}", dto.userId(), dto.postId());
    }
}