package com.weave.recommend.task;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SimilarityComputeTaskTest {

    @Autowired
    private SimilarityComputeTask similarityComputeTask;

    @Test
    public void computeSimilarity() {
        similarityComputeTask.computeSimilarity();
    }
}
