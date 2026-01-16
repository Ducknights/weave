package org.example.model.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@Builder
public class ActivityCardVo {
    private Integer id;
    // 活动标题
    private String title;
    //社团名字
    private String clubName;
    // 活动描述
    private String description;
    // 日期
    private LocalDate date;
    // 时间
    private LocalTime startTime;

    private LocalTime endTime;
    // 地点
    private String location;
}
