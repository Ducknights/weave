package org.example.repository;

import org.example.model.entity.Post;

import java.util.List;

public interface PostRepository {
    List<Post> getPostsFromCacheOrDb(List<Long> ids);
}
