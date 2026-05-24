package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.model.vo.PostDetailVo;

import java.util.List;

public interface PostQueryService {
    List<PostDetailVo> getRecommendPosts();

    Page<PostDetailVo> getNewPosts(int page, int size);

    Page<PostDetailVo> getHotPosts(int page, int size);

    PostDetailVo getPostById(Long id, Long userId);
}
