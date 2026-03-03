package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.dto.PostDto;
import org.example.entity.Post;
import org.example.mapper.PostMapper;
import org.example.service.PostService;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Resource
    private PostMapper postMapper;

    @Override
    public Post createPost(Long userId, PostDto postDto) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        postMapper.insert(post);
        return post;
    }

    @Override
    public Post getPostById(Long id) {
        return postMapper.selectById(id);
    }

    @Override
    public Page<Post> getPostList(int page, int size) {
        Page<Post> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Post> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Post::getStatus, 1)
               .orderByDesc(Post::getCreatedTime);
        return postMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public Post updatePost(Long id, Long userId, PostDto postDto) {
        Post post = postMapper.selectById(id);
        if (post == null || !post.getUserId().equals(userId)) {
            return null;
        }
        post.setTitle(postDto.getTitle());
        post.setContent(postDto.getContent());
        postMapper.updateById(post);
        return post;
    }

    @Override
    public boolean deletePost(Long id, Long userId) {
        Post post = postMapper.selectById(id);
        if (post == null || !post.getUserId().equals(userId)) {
            return false;
        }
        return postMapper.deleteById(id) > 0;
    }

    @Override
    public void incrementViewCount(Long id) {
        Post post = postMapper.selectById(id);
        if (post != null) {
            post.setViewCount(post.getViewCount() + 1);
            postMapper.updateById(post);
        }
    }
}
