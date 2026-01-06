package com.example;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.ClubMainApplication;
import org.example.entity.Club;
import org.example.entity.vo.ClubCardVo;
import org.example.mapper.ClubMapper;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ClubMainApplication.class)
public class AllTest {

    @Resource
    private ClubMapper clubMapper;

    @org.junit.jupiter.api.Test
    public void test() {
        int current = 1;
        int size = 1;
        IPage<Club> clubPage = clubMapper.selectPage(new Page<>(current, size),null);
//        ClubCardVo clubCardVo = ClubCardVo.builder()
//                .id(clubPage.getRecords().get(0).getId())
//                .name(clubPage.getRecords().get(0).getName())
//                .description(clubPage.getRecords().get(0).getDescription())
//                .build();
//        System.out.println(clubCardVo);
        System.out.println(clubPage.getPages());
    }

}
