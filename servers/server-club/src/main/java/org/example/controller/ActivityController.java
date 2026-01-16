/**
 * 活动管理控制器
 * 提供活动的增删改查等基本操作
 */
package org.example.controller;


import jakarta.annotation.Nonnull;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.Activity;
import org.example.model.ClubApiResponse;
import org.example.model.ClubApiStatus;
import org.example.model.vo.ActivityCardVo;
import org.example.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.logging.LoggingRebinder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/club/activities")
public class ActivityController {

    @Resource
    private ActivityService activityService;
    @Autowired
    private LoggingRebinder loggingRebinder;

    /**
     * 创建活动
     * @param activity 活动实体
     * @return 响应结果，包含创建的活动信息
     */
    @PostMapping()
    public ResponseEntity<ClubApiResponse<?>> creatActivity(@Nonnull @RequestBody Activity activity) {
        final Activity newActivity = activityService.creatActivity(activity);
        return ResponseEntity.status(ClubApiStatus.POST_SUCCESS.getCode())
                .body(ClubApiResponse.success(ClubApiStatus.POST_SUCCESS,newActivity));
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
                .body(ClubApiResponse.success(ClubApiStatus.DELETE_SUCCESS,null));
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
                .body(ClubApiResponse.success(ClubApiStatus.PUT_SUCCESS,newActivity));
    }

    /**
     * 根据时间范围查询活动
     * @param startDate 开始日期时间
     * @param endDate 结束日期时间
     * @return 响应结果，包含符合条件的活动列表
     */
    @GetMapping("/week")
    public ResponseEntity<ClubApiResponse<?>> getActivity(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        // 转换为LocalDate，只保留日期部分
        LocalDate start = startDate.toLocalDate();
        LocalDate end = endDate.toLocalDate();

        if (start.isAfter(end)){
            throw new IllegalArgumentException("开始日期必须在结束日期之前");
        }
        log.info("Getting activities between {} and {}", start, end);
        final List<ActivityCardVo> activities = activityService.queryActivity(start, end);
        return ResponseEntity.status(ClubApiStatus.GET_SUCCESS.getCode())
                .body(ClubApiResponse.success(ClubApiStatus.GET_SUCCESS,activities));
    }

    /**
     * 根据ID获取活动信息
     * @param ActivityId 活动ID
     * @return 响应结果，包含指定ID的活动信息
     */
    @GetMapping("{ActivityId}")
    public ResponseEntity<ClubApiResponse<?>> getActivityById(@PathVariable Integer ActivityId) {
        final Activity activity = activityService.queryActivityById(ActivityId);
        return ResponseEntity.status(ClubApiStatus.GET_SUCCESS.getCode())
                .body(ClubApiResponse.success(ClubApiStatus.GET_SUCCESS,activity));
    }
}
