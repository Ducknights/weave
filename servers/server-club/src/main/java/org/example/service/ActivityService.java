package org.example.service;

import org.example.entity.Activity;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityService {
    Activity creatActivity(Activity activity);
    void deleteActivity(Integer ActivityId);
    Activity updateActivity(Activity activity);
    List<Activity> queryActivity(LocalDateTime startDate, LocalDateTime endDate);
    Activity queryActivityById(Integer activityId);
}
