package com.weave.auth.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;


@SpringBootTest
public class ObjectMapperTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test() throws JsonMappingException {
        // 查看 Long 类型的序列化器是什么
        System.out.println(objectMapper.getSerializerProviderInstance()
                .findValueSerializer(Long.class, null));
    }

    @Test
    void testLongToString() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of("id", 1234567890123456789L));
        System.out.println(json); // 应输出 {"id":"1234567890123456789"}
    }

    @Autowired
    private ListableBeanFactory beanFactory;

    @PostConstruct
    @Test
    public void checkMappers() {
        Map<String, ObjectMapper> beans = beanFactory.getBeansOfType(ObjectMapper.class);
        beans.forEach((name, mapper) -> {
            System.out.println("Bean Name: " + name + ", Mapper: " + mapper);
        });
    }
}
