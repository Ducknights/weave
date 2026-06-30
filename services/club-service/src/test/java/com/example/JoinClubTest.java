package com.example;

import jakarta.annotation.Resource;
import org.example.ClubMainApplication;
import org.example.service.ClubService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest(classes = ClubMainApplication.class)
public class JoinClubTest {

    @Resource
    private ClubService clubService;

    @Test
    public void joinClub100Members() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            long userId = random.nextInt(100) + 1;
            int clubId = random.nextInt(8,22);
            clubService.joinClub(clubId, userId);
            System.out.println("用户" + userId + " 加入了社团" + clubId);
        }
        System.out.println("成功添加 100 条加入社团记录");
    }
}
