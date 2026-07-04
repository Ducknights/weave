package com.weave.post.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weave.post.model.dto.PostDto;
import com.weave.post.model.entity.Post;

public interface PostCommandService extends IService<Post> {
    void createPost(Long userId, PostDto postDto);

    void updatePost(Long id, Long userId, PostDto postDto);

    void deletePost(Long id, Long userId);

    void hidePost(Long id, Long userId);

    void restorePost(Long id, Long userId);

    void addToHistory(Long id, Long userId);

    void like(Long userId, Long postId);

    void unLike(Long userId, Long postId);

    void collect(Long userId, Long postId);

    void unCollect(Long userId, Long postId);

    void updateStats(Long postId, String action);
}
