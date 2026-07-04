package com.weave.post.service;

import com.weave.post.model.dto.PostDto;
import com.weave.post.model.entity.Post;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostServiceTest {

    @MockBean
    private InfoEndpoint infoEndpoint;

    @Autowired
    private PostCommandService postCommandService;

    @Autowired
    private PostQueryService postQueryService;

    private static final String[] TITLES = {
            "周末一起去爬山吧",
            "篮球爱好者集合",
            "读书分享会",
            "编程学习小组",
            "摄影外拍活动",
            "周末电影之夜",
            "美食探店分享",
            "健身打卡群",
            "音乐交流分享",
            "旅行攻略讨论",
            "桌游聚会邀请",
            "宠物交流社区",
            "手工艺DIY",
            "瑜伽冥想体验",
            "户外烧烤派对",
            "技术沙龙分享",
            "画画交流群",
            "舞蹈体验课",
            "志愿者活动招募",
            "英语角练习"
    };

    private static final String[] CONTENTS = {
            "周末天气不错，有没有人一起爬山？",
            "有没有喜欢打篮球的朋友，周末约起来！",
            "最近读了一本好书，想和大家分享一下。",
            "想找几个志同道合的朋友一起学习Java。",
            "这周末去公园拍花，有人一起吗？",
            "今晚看一部经典电影，欢迎加入讨论。",
            "发现一家超棒的火锅店，推荐给大家！",
            "坚持健身一个月了，找伙伴一起打卡。",
            "有没有弹吉他的朋友，一起玩音乐。",
            "分享一下去大理的旅行经历和攻略。",
            "周末桌游局，狼人杀、三国杀都有。",
            "养了一只可爱的小猫，分享养宠经验。",
            "最近迷上了手工编织，很有意思。",
            "早起瑜伽，开启美好一天。",
            "夏天来了，组织一次户外烧烤！",
            "聊聊微服务架构的实践经验。",
            "零基础学画画，记录我的进步。",
            "想学街舞，有没有推荐的舞蹈室？",
            "社区志愿服务，帮助需要帮助的人。",
            "每周五晚上英语角，锻炼口语能力。"
    };

    @Disabled
    @Test
    public void createPost() {
        Random random = new Random();
        List<PostDto> posts = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            Long userId = (long) (random.nextInt(100) + 1);
            Long clubId = (long) (random.nextInt(15) + 1);
            String title = TITLES[random.nextInt(TITLES.length)];
            String content = CONTENTS[random.nextInt(CONTENTS.length)];

            PostDto dto = new PostDto();
            dto.setClubId(clubId);
            dto.setTitle(title);
            dto.setContent(content);
            dto.setCoverImage(null);

            posts.add(dto);
            postCommandService.createPost(userId, dto);
        }

        System.out.println("成功创建 " + posts.size() + " 条帖子数据");
    }

    static List<Long> ids = new ArrayList<>();

    @Test
    @Order(1)
    public void testRunWrapper() {
        ids = postCommandService.list().stream().map(Post::getPostId).toList();
        System.out.println(ids.size());
    }

    @Test
     @Order(2)
    @Disabled
    public void like() {
        Random random = new Random();
        for (int i = 0; i < 500; i++) {
            Long userId = (long) (random.nextInt(100) + 1);
            Long postId = ids.get(random.nextInt(ids.size()));
            postCommandService.like(userId, postId);
        }
        System.out.println("成功添加500条点赞数据");
    }

    // @Order(3)
    @Test
    @Disabled
    public void collect() {
        Random random = new Random();
        for (int i = 0; i < 500; i++) {
            Long userId = (long) (random.nextInt(100) + 1);
            Long postId = ids.get(random.nextInt(ids.size()));
            postCommandService.collect(userId, postId);
        }
        System.out.println("成功添加500条收藏数据");
    }

    @Test
    @Order(4)
    public void view() {
        Random random = new Random();
        for (int i = 0; i < 1000; i++) {
            Long userId = (long) (random.nextInt(100) + 1);
            Long postId = ids.get(random.nextInt(ids.size()));
            postQueryService.clickForDetails(postId, userId);
        }
        System.out.println("成功添加1000条浏览数据");
    }
}
