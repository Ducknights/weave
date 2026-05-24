package org.example.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.MQueue;
import org.example.model.dto.PostDto;
import org.example.model.PostActionMessage;
import org.example.model.enums.PostApiStatus;
import org.example.model.vo.PostDetailVo;
import org.example.service.PostCommandService;
import org.example.service.PostQueryService;
import org.example.util.SecurityUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log4j2
@RestController
@RequestMapping("/api/post")
public class PostController {

    @Resource
    private PostCommandService postCommandService;
    @Resource
    private PostQueryService postQueryService;

    /**
     * 创建新帖子的请求处理方法
     * 通过POST请求接收前端提交的帖子数据
     *
     * @param postDto 包含帖子信息的DTO对象，包含标题、内容等信息
     * @return 返回创建成功的帖子对象，包含系统生成的ID等信息
     */
    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostDto postDto) {
        Long userId = SecurityUtils.getCurrentUserId();
        postCommandService.createPost(userId, postDto);
        return ResponseEntity.ok(PostApiStatus.CREATE_SUCCESS.response());
    }

    /**
     * 获取推荐帖子的请求处理方法
     * 通过GET请求获取推荐的帖子列表
     *
     * @return 返回推荐的帖子对象列表，如果列表为空则返回404
     */
    @GetMapping("/recommend")
    public ResponseEntity<?> getRecommendPosts() {
        List<PostDetailVo> postVos = postQueryService.getRecommendPosts();
        if (postVos == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postVos);
    }

    /**
     * 获取热门帖子的请求处理方法
     * 通过GET请求获取热门的帖子列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 返回热门的帖子对象列表，如果列表为空则返回404
     */
    @GetMapping("/hot")
    public ResponseEntity<?> getHotPosts(@RequestParam int pageNum, @RequestParam int pageSize) {
        Page<PostDetailVo> postVos = postQueryService.getHotPosts(pageNum, pageSize);
        if (postVos == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(postVos);
    }

    /**
     * 获取最新帖子的请求处理方法
     * 通过GET请求获取最新的帖子列表
     *
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 返回最新的帖子对象列表
     */
    @GetMapping("/new")
    public ResponseEntity<Page<PostDetailVo>> getNewPosts(@RequestParam int pageNum, @RequestParam int pageSize) {
        Page<PostDetailVo> postVos = postQueryService.getNewPosts(pageNum, pageSize);
        return ResponseEntity.ok(postVos);
    }

    /**
     * 获取指定ID的帖子的请求处理方法
     * 通过GET请求获取指定ID的帖子信息
     *
     * @param id 帖子的ID
     * @return 返回指定ID的帖子对象，如果帖子不存在则返回404
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        PostDetailVo post = postQueryService.getPostById(id,userId);
        return ResponseEntity.ok(post);
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
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto postDto) {
        Long userId = SecurityUtils.getCurrentUserId();
        postCommandService.updatePost(id, userId, postDto);
        return ResponseEntity.ok(PostApiStatus.UPDATE_SUCCESS.response());
    }

    /**
     * 删除指定ID的帖子的请求处理方法
     * 通过DELETE请求删除指定ID的帖子
     *
     * @param id 帖子的ID
     * @return 返回删除成功的消息，如果帖子不存在则返回404
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        Long userId = SecurityUtils.getCurrentUserId();
        postCommandService.deletePost(id, userId);
        return ResponseEntity.ok(PostApiStatus.DELETE_SUCCESS.response());
    }

    /**
     * 监听帖子行为消息，异步更新统计数据
     */
    @RabbitListener(queues = MQueue.POST_ACTION_QUEUE)
    public void handlePostAction(PostActionMessage message) {
        try {
            log.info("收到帖子行为消息: userId={}, postId={}, action={}, increment={}", 
                    message.getUserId(), message.getPostId(), message.getAction(), message.getIncrement());
            // 更新帖子统计信息
            postCommandService.updateStats(message.getPostId(), message.getAction(), message.getIncrement());
        } catch (Exception e) {
            log.error("处理帖子行为消息失败", e);
        }
    }
}
