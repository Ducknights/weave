package org.example.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;
import org.example.model.dto.RelationDto;
import org.example.model.entity.UserRelations;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface RelationMapper extends BaseMapper<UserRelations> {

    default int deleteRecord(RelationDto dto){
        return delete(new LambdaQueryWrapper<UserRelations>()
                .eq(UserRelations::getUserId, dto.userId())
                .eq(UserRelations::getTargetId, dto.targetId())
                .eq(UserRelations::getType, dto.type()));
    }

    default Set<Long> getRecord(RelationDto dto, int page, int size){
        IPage<UserRelations> iPage = selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<UserRelations>()
                        .eq(UserRelations::getUserId, dto.userId())
                        .eq(dto.targetId() != null, UserRelations::getTargetId, dto.targetId())
                        .eq(dto.type() != null, UserRelations::getType, dto.type())
                        .orderByDesc(UserRelations::getCreatedTime));
        return iPage.getRecords().stream().map(UserRelations::getTargetId).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    default Set<Long> getAllTargetIdsByUserAndType(RelationDto dto) {
        List<UserRelations> list = selectList(
                new LambdaQueryWrapper<UserRelations>()
                        .eq(UserRelations::getUserId, dto.userId())
                        .eq(UserRelations::getType, dto.type())
                        .select(UserRelations::getTargetId)
        );
        return list.stream().map(UserRelations::getTargetId).collect(Collectors.toSet());
    }
}
