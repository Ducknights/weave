package com.weave.post.repository;

import com.weave.post.model.entity.Post;

import java.util.List;

public interface PostRepository {
    List<Post> getPostsFromCacheOrDb(List<Long> ids);
}
