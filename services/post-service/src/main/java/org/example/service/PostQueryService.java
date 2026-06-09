package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.dto.PostDetailVo;

import java.util.List;
import java.util.Map;

public interface PostQueryService {
    List<PostDetailVo> getRecommendPosts(Long userId);

    Page<PostDetailVo> getNewPosts(int page, int size);

    Page<PostDetailVo> getHotPosts(int page, int size);

    PostDetailVo getPostById(Long id, Long userId);

    List<PostDetailVo> getPostsByIds(List<Long> ids);
}
