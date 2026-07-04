package com.weave.post.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import com.weave.post.model.entity.PostResource;

import java.util.List;

@Mapper
public interface PostResourceMapper extends BaseMapper<PostResource> {

    @Select("<script>" +
            "SELECT * FROM post_resource WHERE post_id IN " +
            "<foreach collection='list' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<PostResource> selectByPostIds(List<Long> postIds);
}
