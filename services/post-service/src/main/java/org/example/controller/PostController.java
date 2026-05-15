package org.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.MQueue;
import org.example.dto.PostDto;
import org.example.entity.Post;
import org.example.model.PostActionMessage;
import org.example.service.PostService;
import org.example.util.SecurityUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Log4j2
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
        Long userId = SecurityUtils.getCurrentUserId();
        Post post = postService.createPost(userId, postDto);
        return ResponseEntity.ok(post);
    }

    /**
     * 获取指定ID的帖子的请求处理方法
     * 通过GET请求获取指定ID的帖子信息
     *
     * @param id 帖子的ID
     * @return 返回指定ID的帖子对象，如果帖子不存在则返回404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Post post = postService.getPostById(id);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        postService.incrementViewCount(id);
        return ResponseEntity.ok(post);
    }

    /**
     * 获取帖子列表的请求处理方法
     * 通过GET请求获取指定页码和每页大小的帖子列表
     *
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @return 返回指定页码和每页大小的帖子列表
     */
    @GetMapping("/list")
    public ResponseEntity<Page<Post>> getPostList(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Post> postPage = postService.getPostList(page, size);
        return ResponseEntity.ok(postPage);
    }

    /**
     * 更新指定ID的帖子的请求处理方法
     * 通过PUT请求更新指定ID的帖子信息
     *
     * @param id 帖子的ID
     * @param postDto 包含帖子信息的DTO对象，包含标题、内容等信息
     * @return 返回更新成功的帖子对象，如果帖子不存在则返回404
     */
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto postDto) {
        Long userId = SecurityUtils.getCurrentUserId();
        Post post = postService.updatePost(id, userId, postDto);
        if (post == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean success = postService.deletePost(id, userId);
        if (!success) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 点赞/取消点赞帖子
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean isLiked = postService.toggleLike(userId, id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("isLiked", isLiked);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 收藏/取消收藏帖子
     */
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Map<String, Object>> toggleFavorite(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean isFavorited = postService.toggleFavorite(userId, id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("isFavorited", isFavorited);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 分享帖子
     */
    @PostMapping("/{id}/share")
    public ResponseEntity<Map<String, Object>> sharePost(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        boolean success = postService.sharePost(userId, id);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", success);
        
        return ResponseEntity.ok(result);
    }

    /**
     * 监听帖子行为消息，异步更新统计数据
     */
    @RabbitListener(queues = MQueue.POST_ACTION_QUEUE)
    public void handlePostAction(PostActionMessage message) {
        try {
            log.info("收到帖子行为消息: userId={}, postId={}, action={}, increment={}", 
                    message.getUserId(), message.getPostId(), message.getAction(), message.getIncrement());
            postService.updateStats(message.getPostId(), message.getAction(), message.getIncrement());
        } catch (Exception e) {
            log.error("处理帖子行为消息失败", e);
        }
    }
}
