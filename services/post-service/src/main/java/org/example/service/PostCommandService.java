package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.dto.PostDto;
import org.example.model.entity.Post;

public interface PostCommandService extends IService<Post> {
    void createPost(Long userId, PostDto postDto);

    void updatePost(Long id, Long userId, PostDto postDto);

    void deletePost(Long id, Long userId);

    void addToHistory(Long id, Long userId);

    void like(Long userId, Long postId);

    void unlike(Long userId, Long postId);

    void collect(Long userId, Long postId);

    void uncollect(Long userId, Long postId);

    void updateStats(Long postId, String action);
}
