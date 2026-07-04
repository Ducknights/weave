package com.weave.comment.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.weave.comment.model.dto.CommentVosDto;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

@Log4j2
@SpringBootTest
class CommentServiceImplTest {

    public Long postId = 2066753951299358722L;

    @Autowired
    private CommentServiceImpl commentService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Test
    void getRootCommentsByPostId() {
        CommentVosDto commentVosDto = commentService.getRootCommentsByPostByHot(postId, null, null, 10);
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String jsonString = objectMapper.writeValueAsString(commentVosDto);
            System.out.println(jsonString);
        } catch (Exception e) {
            log.error("Error occurred: {}", e.getMessage());
        }
    }

    @Test
    void test(){
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> null);
    }
}