package org.example.service;

import org.example.entity.Activity;
import org.example.model.vo.ActivityCardVo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ActivityService {
    Activity creatActivity(Activity activity);
    void deleteActivity(Integer ActivityId);
    Activity updateActivity(Activity activity);
    List<ActivityCardVo> queryActivity(LocalDate startDate, LocalDate endDate);
    Activity queryActivityById(Integer activityId);
}
