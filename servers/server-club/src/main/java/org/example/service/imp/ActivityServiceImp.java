package org.example.service.imp;

import lombok.extern.log4j.Log4j2;
import org.example.entity.Activity;
import org.example.mapper.ActivityMapper;
import org.example.model.vo.ActivityCardVo;
import org.example.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class ActivityServiceImp implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public Activity creatActivity(Activity activity) {
        if(activityMapper.insert(activity)>0){
            return activity;
        }
        return null;
    }

    @Override
    public void deleteActivity(Integer ActivityId) {
        activityMapper.deleteById(ActivityId);
    }

    @Override
    public Activity updateActivity(Activity activity) {
        return activityMapper.updateActivity(activity);
    }

    @Override
    public List<ActivityCardVo> queryActivityByDate(LocalDate startDate, LocalDate endDate) {
        String key = "activities:" + startDate + ":" + endDate;
        // 检查缓存
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            log.info("Cache hit");
            return (List<ActivityCardVo>) cached;
        }

        // 使用分布式锁防止缓存击穿
        String lockKey = "lock:" + key;
        try {
            // 尝试获取锁
            Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
            // 获取到锁
            if (locked != null && locked) {
                List<ActivityCardVo> activities = activityMapper.queryActivity(startDate, endDate);
                // 设置缓存，并添加过期时间
                redisTemplate.opsForValue().set(key, activities, 1, TimeUnit.HOURS);
                return activities;
            }
            // 等待100毫秒重试获取
            Thread.sleep(100);
            return queryActivityByDate(startDate, endDate);
        } catch (Exception e) {
            log.error("Error occurred while querying activities: {}", e.getMessage());
            return Collections.emptyList();
        } finally {
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    public Activity queryActivityById(Integer activityId) {
        return activityMapper.selectById(activityId);
    }
}
