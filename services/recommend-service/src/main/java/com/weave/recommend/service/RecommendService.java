package com.weave.recommend.service;

import java.util.List;

public interface RecommendService {
    List<Long> recommend(Long userId, int limit);
    void computePostSimilarity();
}
