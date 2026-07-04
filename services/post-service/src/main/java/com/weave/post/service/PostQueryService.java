package com.weave.post.service;

import com.weave.model.model.dto.PostDetailVo;

import java.util.List;

public interface PostQueryService {
    List<PostDetailVo> getRecommendPosts(Long userId,Integer limit);

    List<PostDetailVo> getNewPosts(int page, int size);

    List<PostDetailVo> getHotPosts(int page, int size);

    List<PostDetailVo> clickForDetails(Long id, Long userId);

    List<PostDetailVo> getPostsByIds(List<Long> ids);

    List<PostDetailVo> getHiddenPostsByUserId(Long userId);

    List<PostDetailVo> getPostsByUser(Long userId);
}
