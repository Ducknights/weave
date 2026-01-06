package org.example.entity.vo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;


@Data
@Builder
public class ActivityCardVo {
    private Integer id;
    private String name;
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String location;
}
