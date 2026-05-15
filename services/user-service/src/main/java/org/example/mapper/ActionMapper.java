package org.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.example.dto.ActionDto;
import org.example.entity.UserActions;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface ActionMapper extends BaseMapper<UserActions> {

    default void deleteRecord(ActionDto dto) {
        delete(new LambdaQueryWrapper<UserActions>()
                .eq(UserActions::getUserId, dto.userId())
                .eq(UserActions::getTargetId, dto.targetId())
                .eq(UserActions::getType, dto.type()));
    }

    default Set<Long> getRecord(ActionDto dto, int page, int size){
                IPage<UserActions> pageParam = selectPage(new Page<>(page, size),
                        new LambdaQueryWrapper<UserActions>()
                        .eq(UserActions::getUserId, dto.userId())
                        .eq(UserActions::getTargetId, dto.targetId())
                        .eq(UserActions::getType, dto.type()));
        return pageParam.getRecords().stream().map(UserActions::getTargetId).collect(Collectors.toSet());
    }

}
