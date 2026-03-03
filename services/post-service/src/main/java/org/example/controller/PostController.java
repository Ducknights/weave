package org.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.dto.PostDto;
import org.example.entity.Post;
import org.example.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Resource
    private PostService postService;

    /**
     * 创建新帖子的请求处理方法
     * 通过POST请求接收前端提交的帖子数据
     *
     * @param postDto 包含帖子信息的DTO对象，包含标题、内容等信息
     * @return 返回创建成功的帖子对象，包含系统生成的ID等信息
     */
    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestBody PostDto postDto) {
        Long userId = 1L;
        Post post = postService.createPost(userId, postDto);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        postService.incrementViewCount(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/list")
    public ResponseEntity<Page<Post>> getPostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Post> postPage = postService.getPostList(page, size);
        return ResponseEntity.ok(postPage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody PostDto postDto) {
        Post post = postService.updatePost(id, userId, postDto);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        boolean success = postService.deletePost(id, userId);
        if (!success) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }
}
