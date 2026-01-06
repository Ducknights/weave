/**
 * 活动管理控制器
 * 提供活动的增删改查等基本操作
 */
package org.example.controller;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import org.example.entity.Activity;
import org.example.model.ClubApiResponse;
import org.example.model.ClubApiStatus;
import org.example.service.ActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/clubs/activities")
public class ActivityController {

    @Resource
    private ActivityService activityService;

    /**
     * 创建活动
     * @param activity 活动实体
     * @return 响应结果，包含创建的活动信息
     */
    @PostMapping()
    public ResponseEntity<ClubApiResponse<?>> creatActivity(@Nonnull @RequestBody Activity activity) {
        final Activity newActivity = activityService.creatActivity(activity);
        return ResponseEntity.status(ClubApiStatus.POST_SUCCESS.getCode())
                .body(ClubApiResponse.postSuccess(newActivity));
    }

    /**
     * 删除活动
     * @param ActivityId 活动ID
     * @return 响应结果
     */
    @DeleteMapping()
    public ResponseEntity<ClubApiResponse<?>> deleteActivity(@Nonnull @RequestBody Integer ActivityId) {
        activityService.deleteActivity(ActivityId);
        return ResponseEntity.status(ClubApiStatus.DELETE_SUCCESS.getCode())
                .body(ClubApiResponse.deleteSuccess());
    }

    /**
     * 更新活动信息
     * @param activity 活动实体
     * @return 响应结果，包含更新后的活动信息
     */
    @PutMapping()
    public ResponseEntity<ClubApiResponse<?>> updateActivity(@Nonnull @RequestBody Activity activity) {
        final Activity newActivity = activityService.updateActivity(activity);
        return ResponseEntity.status(ClubApiStatus.PUT_SUCCESS.getCode())
                .body(ClubApiResponse.putSuccess(newActivity));
    }

    /**
     * 根据时间范围查询活动
     * @param startDate 开始日期时间
     * @param endDate 结束日期时间
     * @return 响应结果，包含符合条件的活动列表
     */
    @GetMapping()
    public ResponseEntity<ClubApiResponse<?>> getActivity(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        if (startDate.isBefore(endDate)){
            throw new IllegalArgumentException("开始日期必须在结束日期之前");
        }
        final List<Activity> activities = activityService.queryActivity(startDate, endDate);
        return ResponseEntity.status(ClubApiStatus.GET_SUCCESS.getCode())
                .body(ClubApiResponse.getSuccess(activities));
    }
}
