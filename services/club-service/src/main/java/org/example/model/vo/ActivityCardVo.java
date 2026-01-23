package org.example.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityCardVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Integer id;
    // 活动标题
    private String title;
    //社团名字
    private String clubName;
    // 日期
    private LocalDate date;
    // 开始时间
    private LocalTime startTime;
    // 结束时间
    private LocalTime endTime;
    // 地点
    private String location;
}
