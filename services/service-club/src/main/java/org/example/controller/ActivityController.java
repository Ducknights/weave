package org.example.controller;


import org.example.entity.Activity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @GetMapping("/week")
    public Activity getActivity(@RequestParam LocalDateTime startDate,
                                @RequestParam LocalDateTime endDate) {
        if (startDate.isBefore(endDate)){
            throw new IllegalArgumentException("开始日期必须在结束日期之前");
        }
        return ;
    }
}
