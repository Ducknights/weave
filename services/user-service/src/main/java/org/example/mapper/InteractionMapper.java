package org.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.dto.InteractionDto;

import java.util.Set;

@Mapper
public interface InteractionMapper extends BaseMapper<InteractionDto> {
    default void deleteRecord(InteractionDto dto){
        delete(new LambdaQueryWrapper<InteractionDto>()
                .eq(InteractionDto::userId, dto.userId())
                .eq(InteractionDto::targetId, dto.targetId())
                .eq(InteractionDto::type, dto.type()));
    }

    default Set<Long> getRecord(InteractionDto dto, int page, int size){
        return null;
    }
}
