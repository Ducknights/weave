package org.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.dto.InteractionDto;

@Mapper
public interface InteractionMapper extends BaseMapper<InteractionDto> {
    default void deleteRecord(InteractionDto dto){
        delete(new LambdaQueryWrapper<InteractionDto>()
                .eq(InteractionDto::userId, dto.userId())
                .eq(InteractionDto::targetId, dto.targetId())
                .eq(InteractionDto::type, dto.type()));
    }
}
