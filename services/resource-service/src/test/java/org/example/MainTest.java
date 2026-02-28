package org.example;



import jakarta.annotation.Resource;
import org.example.service.FileService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MainTest {

    @Resource
    private FileService fileService;

    @Test
    public void test() {
       fileService.uploadFile(null);
    }
}
