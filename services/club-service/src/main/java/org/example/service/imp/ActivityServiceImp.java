package org.example.service.imp;

import lombok.extern.log4j.Log4j2;
import org.example.entity.Activity;
import org.example.mapper.ActivityMapper;
import org.example.model.ClubApiResponse;
import org.example.model.ClubApiStatus;
import org.example.model.vo.ActivityCardVo;
import org.example.service.ActivityService;
import org.example.constant.CacheKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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

    @Override
    public Activity creatActivity(Activity activity) {
        if(activityMapper.insert(activity)>0){
            return activity;
        }
        return null;
    }

    @Override
    @CachePut(value = CacheKey.ACTIVITY_AREA, key ="'activityId:'+ #activityId")
    public ClubApiResponse<?> deleteActivity(Integer activityId) {
        try {
            activityMapper.deleteById(activityId);
            return ClubApiStatus.DELETE_SUCCESS.response();
        } catch (Exception e) {
            log.error("删除活动失败，参数： {}", activityId, e);
            throw new RuntimeException("删除活动失败");
        }
    }

    @Override
    @CacheEvict(value = CacheKey.ACTIVITY_AREA, key ="'activityId:'+ #activity.id")
    public Activity updateActivity(Activity activity) {
        return activityMapper.updateActivity(activity);
    }

    @Override
    @Cacheable(value = CacheKey.ACTIVITY_AREA, key ="'activities:'+ #startDate +':' + #endDate")
    public List<ActivityCardVo> queryActivityByDate(LocalDate startDate, LocalDate endDate) {
        log.info("从数据库查询活动，参数： {} to {}", startDate, endDate);
        return activityMapper.queryActivity(startDate, endDate);
    }

    @Cacheable(value = CacheKey.ACTIVITY_AREA, key ="'activityId:'+ #activityId ")
    @Override
    public Activity queryActivityById(Integer activityId) {
        return activityMapper.selectById(activityId);
    }
}
