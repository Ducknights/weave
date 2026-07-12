package com.weave.draft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.weave.draft.model.entity.DraftResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DraftResourceMapper extends BaseMapper<DraftResource> {

    @Select("<script>" +
            "SELECT * FROM draft_resource WHERE draft_id IN " +
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<DraftResource> selectByDraftIds(List<Long> draftIds);

    @Select("SELECT * FROM draft_resource WHERE draft_id = #{draftId}")
    List<DraftResource> selectByDraftId(Long draftId);
}
