package com.weave.club.service;

import com.weave.club.model.entity.Activity;
import com.weave.club.model.vo.ActivityCardVo;

import java.time.LocalDate;
import java.util.List;

public interface ActivityService {
    Activity creatActivity(Activity activity);
    void deleteActivity(Integer ActivityId);
    Activity updateActivity(Activity activity);
    List<ActivityCardVo> queryActivityByDate(LocalDate startDate, LocalDate endDate);
    Activity queryActivityById(Integer activityId);

    List<Activity> getActivitiesByClubId(Integer clubId);
}
