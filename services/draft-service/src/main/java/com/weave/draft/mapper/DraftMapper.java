package com.weave.draft.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weave.draft.model.entity.Draft;
import com.weave.draft.model.enums.DraftStatus;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DraftMapper extends BaseMapper<Draft> {

    /** 查询用户指定状态的草稿 */
    default List<Draft> selectByUserIdAndStatus(Long userId, DraftStatus status) {
        LambdaQueryWrapper<Draft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Draft::getUserId, userId)
                .eq(Draft::getStatus, status)
                .orderByDesc(Draft::getUpdatedTime);
        return this.selectList(queryWrapper);
    }

    /** 查询用户全部草稿 */
    default List<Draft> selectByUserId(Long userId) {
        LambdaQueryWrapper<Draft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Draft::getUserId, userId)
                .orderByDesc(Draft::getUpdatedTime);
        return this.selectList(queryWrapper);
    }

    /** 查询所有指定状态的草稿（审核用） */
    default List<Draft> selectByStatus(DraftStatus status) {
        LambdaQueryWrapper<Draft> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Draft::getStatus, status)
                .orderByAsc(Draft::getUpdatedTime);
        return this.selectList(queryWrapper);
    }
}
