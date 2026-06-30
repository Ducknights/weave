package com.example;

import jakarta.annotation.Resource;
import org.example.ClubMainApplication;
import org.example.model.entity.Club;
import org.example.service.ClubService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ClubMainApplication.class)
public class ClubCreateTest {

    @Resource
    private ClubService clubService;

    private static final String[][] CLUB_DATA = {
            {"Java编程社", "专注于Java技术交流与学习，每周举办技术分享会"},
            {"Python爱好者", "Python数据处理、机器学习与自动化脚本开发"},
            {"前端技术圈", "HTML/CSS/JavaScript/Vue/React 前端技术交流"},
            {"算法竞赛队", "LeetCode刷题、ACM竞赛训练与算法讨论"},
            {"数据库研究会", "MySQL/Redis/MongoDB 数据库技术深入学习"},
            {"人工智能实验室", "AI、深度学习、自然语言处理技术研究"},
            {"网络安全小组", "CTF竞赛、Web安全、渗透测试技术交流"},
            {"移动开发社", "Android/iOS/Flutter 跨平台移动开发"},
            {"云计算与DevOps", "Docker/K8s/Jenkins CI/CD 技术实践"},
            {"开源贡献者", "参与开源项目贡献代码，学习开源协作流程"},
            {"产品设计社", "产品思维、UI/UX设计、需求分析方法论"},
            {"游戏开发圈", "Unity/Unreal 游戏引擎开发与游戏设计"},
            {"大数据分析", "Hadoop/Spark/Flink 大数据处理技术栈"},
            {"区块链研究", "Web3、智能合约、去中心化应用技术探索"},
            {"硬件极客", "嵌入式开发、IoT物联网、树莓派创意项目"}
    };

    @Test
    public void create15Clubs() {
        for (String[] data : CLUB_DATA) {
            Club club = Club.builder()
                    .name(data[0])
                    .description(data[1])
                    .build();
            Club created = clubService.createClub(club);
            System.out.println("创建俱乐部: id=" + created.getId() + ", name=" + created.getName());
        }
        System.out.println("成功创建 15 个俱乐部");
    }
}
