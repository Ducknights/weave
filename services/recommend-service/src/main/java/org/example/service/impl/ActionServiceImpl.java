package org.example.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.mapper.UserActionMapper;
import org.example.model.dto.ActionDto;
import org.example.model.entity.UserAction;
import org.example.service.ActionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ActionServiceImpl implements ActionService {

    @Resource
    private UserActionMapper userActionMapper;

    @Override
    public void addRecord(ActionDto dto) {
        UserAction action = new UserAction();
        action.setUserId(dto.userId());
        action.setTargetId(dto.targetId());
        action.setType(dto.type());
        action.setCreatedTime(LocalDateTime.now());
        userActionMapper.insert(action);
        log.debug("添加用户行为记录: userId={}, targetId={}, type={}", dto.userId(), dto.targetId(), dto.type());
    }

    @Override
    public void deleteRecord(ActionDto dto) {
        userActionMapper.deleteByUserIdAndTargetId(dto.userId(), dto.targetId());
        log.debug("删除用户行为记录: userId={}, targetId={}", dto.userId(), dto.targetId());
    }
}