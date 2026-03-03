package org.example.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.dto.PostDto;
import org.example.entity.Post;

public interface PostService {
    Post createPost(Long userId, PostDto postDto);

    Post getPostById(Long id);

    Page<Post> getPostList(int page, int size);

    Post updatePost(Long id, Long userId, PostDto postDto);

    boolean deletePost(Long id, Long userId);

    void incrementViewCount(Long id);
}
