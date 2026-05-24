package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.model.entity.PostResource;

@Mapper
public interface PostResourceMapper extends BaseMapper<PostResource> {
}
