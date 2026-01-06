package org.example.service.imp;

import jakarta.annotation.Resource;
import org.example.entity.Activity;
import org.example.mapper.ActivityMapper;
import org.example.service.ActivityService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityServiceImp implements ActivityService {

    @Resource
    private ActivityMapper activityMapper;

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
    public List<Activity> queryActivity(LocalDateTime startDate, LocalDateTime endDate) {
        return activityMapper.queryActivity(startDate, endDate);
    }
}
