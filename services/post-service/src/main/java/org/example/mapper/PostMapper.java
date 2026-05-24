package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.example.model.entity.Post;

@Mapper
public interface PostMapper extends BaseMapper<Post> {

    @Update("UPDATE post SET view_count = view_count + 1 WHERE id = #{id}")
    void increaseViewCount(Long id);

    @Update("UPDATE post SET like_count = GREATEST(0, like_count + #{delta}) WHERE id = #{id}")
    void updateLikeCount(@Param("id") Long id, @Param("delta") int delta);

    @Update("UPDATE post SET collect_count = GREATEST(0, collect_count + #{delta}) WHERE id = #{id}")
    void updateCollectCount(@Param("id") Long id, @Param("delta") int delta);

    @Update("UPDATE post SET share_count = GREATEST(0, share_count + #{delta}) WHERE id = #{id}")
    void updateShareCount(@Param("id") Long id, @Param("delta") int delta);

    @Update("UPDATE post SET comment_count = GREATEST(0, comment_count + #{delta}) WHERE id = #{id}")
    void updateCommentCount(@Param("id") Long id, @Param("delta") int delta);
}
