package org.example.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.dto.InteractionDto;
import org.example.mapper.InteractionMapper;
import org.example.service.InteractionService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class InteractionServiceImpl implements InteractionService {

    @Resource
    private InteractionMapper interactionMapper;

    @Override
    public void addRecord(InteractionDto dto) {
        try{
            interactionMapper.insert(dto);
        }catch (DuplicateKeyException e) {
            log.debug("用户 {} 重复执行操作，目标ID: {}，操作类型: {}", dto.getUserId(), dto.getTargetId(), dto.getType());
        }catch(DataIntegrityViolationException e){
            log.error("执行操作失败，用户ID: {}, 目标ID: {}，操作类型: {}", dto.getUserId(), dto.getTargetId(), dto.getType(), e);
        }
    }

    @Override
    public void deleteRecord(InteractionDto dto) {
        interactionMapper.deleteRecord(dto);
    }

    @Override
    public List<Long> getRecord(InteractionDto dto) {
        return List.of();
    }
}
