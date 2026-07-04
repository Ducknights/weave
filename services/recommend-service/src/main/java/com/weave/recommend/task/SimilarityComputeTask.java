package com.weave.recommend.task;

import com.weave.recommend.service.RecommendService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SimilarityComputeTask {

    @Resource
    private RecommendService recommendService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void computeSimilarity() {
        log.info("定时任务：开始计算帖子相似度");
        try {
            recommendService.computePostSimilarity();
            log.info("定时任务：帖子相似度计算完成");
        } catch (Exception e) {
            log.error("定时任务：帖子相似度计算失败", e);
        }
    }
}
