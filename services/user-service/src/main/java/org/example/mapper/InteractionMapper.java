package org.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.dto.UserInteractionDto;

@Mapper
public interface InteractionMapper extends BaseMapper<UserInteractionDto> {
    default void deleteRecord(UserInteractionDto dto){
        delete(new LambdaQueryWrapper<UserInteractionDto>()
                .eq(UserInteractionDto::getUserId, dto.getUserId())
                .eq(UserInteractionDto::getTargetId, dto.getTargetId())
                .eq(UserInteractionDto::getType, dto.getType()));
    }
}
