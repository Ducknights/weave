package com.example;

import jakarta.annotation.Resource;
import org.example.ClubMainApplication;
import org.example.model.entity.Activity;
import org.example.service.ActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Random;

@SpringBootTest(classes = ClubMainApplication.class)
public class CreateActivityTest {

    @Resource
    private ActivityService activityService;

    private static final String[] TITLES = {
            "迎新见面会",
            "技术分享沙龙",
            "编程马拉松",
            "项目实战训练营",
            "读书交流会",
            "户外团建活动",
            "线上技术讲座",
            "代码评审工作坊",
            "AI技术分享会",
            "开源项目实战",
            "职业规划分享",
            "前端技术交流会",
            "后端架构讨论",
            "数据分析实战",
            "移动开发Workshop",
            "云计算实践",
            "安全攻防演练",
            "产品设计圆桌",
            "游戏开发Jam",
            "区块链技术入门"
    };

    private static final String[] LOCATIONS = {
            "3号教学楼301教室",
            "图书馆研讨室A",
            "大学生活动中心101",
            "2号教学楼多媒体教室",
            "体育馆活动室",
            "线上腾讯会议",
            "综合楼报告厅",
            "创客空间",
            "4号教学楼阶梯教室",
            "实验楼机房1"
    };

    private static final String[] DESCRIPTIONS = {
            "欢迎新同学加入，互相认识交流",
            "邀请嘉宾分享最新技术趋势和实践经验",
            "限时编程挑战，组队完成项目开发",
            "动手实践项目，提升工程能力",
            "一起阅读经典技术书籍，分享感悟",
            "走出教室，在户外进行团队协作",
            "远程连线大咖，在线技术讲座",
            "互相评审代码，学习最佳实践",
            "探讨AI前沿技术，动手实操练习",
            "参与真实开源项目，积累贡献经验"
    };

    @Test
    public void create50Activities() {
        Random random = new Random();
        LocalDate today = LocalDate.now();
        LocalDate twoWeeksLater = today.plusDays(14);

        for (int i = 0; i < 50; i++) {
            int clubId = random.nextInt(8,22);
            String title = TITLES[random.nextInt(TITLES.length)];

            // 从今天到两周内的随机日期
            long daysBetween = twoWeeksLater.toEpochDay() - today.toEpochDay();
            LocalDate date = today.plusDays(random.nextInt((int) daysBetween + 1));

            // 随机开始时间 8:00-18:00 整点
            int startHour = random.nextInt(11) + 8;
            LocalTime startTime = LocalTime.of(startHour, 0);
            // 结束时间 开始后1-3小时
            LocalTime endTime = startTime.plusHours(random.nextInt(3) + 1);

            String location = LOCATIONS[random.nextInt(LOCATIONS.length)];
            String description = DESCRIPTIONS[random.nextInt(DESCRIPTIONS.length)];

            Activity activity = new Activity();
            activity.setClubId(clubId);
            activity.setTitle(title);
            activity.setDate(date);
            activity.setStartTime(startTime);
            activity.setEndTime(endTime);
            activity.setLocation(location);
            activity.setDescription(description);

            Activity created = activityService.creatActivity(activity);
            System.out.println("活动创建成功: id=" + created.getId()
                    + ", clubId=" + clubId
                    + ", date=" + date
                    + ", " + startTime + "-" + endTime
                    + ", title=" + title);
        }
        System.out.println("成功创建 50 条活动数据");
    }
}
