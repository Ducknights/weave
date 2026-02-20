package org.example.service;

import java.util.List;

public interface LikeService {
    void likePost(Long userId, Long postId);

    void unlikePost(Long userId, Long postId);

    List<Long> getUserLikedPost(Long userId);
}
