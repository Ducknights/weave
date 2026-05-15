package org.example.model.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论实体类
 * 映射到 MongoDB 的 comments 集合
 */
@Builder
@Data
@Document(collection = "comments")
public class Comment {
    private ObjectId id;
    private String resourceId;  // 资源ID
    private String parentId;    // 父评论ID
    private Long userId;    // 用户ID
    private String avatar;    // 用户头像
    private String userName;    // 用户名
    private String content;    // 评论内容
    
    @Default
    private int replyCount = 0;   // 回复数量
    
    @Default
    private int likeCount = 0;    // 点赞数量
    
    @Default
    private List<String> likedUsers = new ArrayList<>();    // 点赞用户ID列表
    
    @Default
    private LocalDateTime createdTime = LocalDateTime.now();
    
    @Default
    private LocalDateTime updatedTime = LocalDateTime.now();
    
    @Default
    private int status = STATUS_VISIBLE;
    
    // 评论状态常量
    public static final int STATUS_HIDDEN = 0; // 隐藏
    public static final int STATUS_VISIBLE = 1; // 可见
    public static final int STATUS_DELETED = 2; // 删除
    
    /**
     * 验证评论数据
     */
    public static ValidationResult validate(Comment comment) {
        ValidationResult result = new ValidationResult();
        
        if (comment.getResourceId() == null || comment.getResourceId().isEmpty()) {
            result.addError("缺少资源ID");
        }
        
        if (comment.getUserId() == null) {
            result.addError("缺少用户ID");
        }
        
        if (comment.getUserName() == null || comment.getUserName().isEmpty()) {
            result.addError("缺少用户名");
        }
        
        if (comment.getContent() == null || comment.getContent().trim().isEmpty()) {
            result.addError("评论内容不能为空");
        } else if (comment.getContent().length() > 1000) {
            result.addError("评论内容不能超过1000字符");
        }
        
        return result;
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private boolean isValid = true;
        private List<String> errors = new ArrayList<>();
        
        public void addError(String error) {
            errors.add(error);
            isValid = false;
        }
        
        public boolean isValid() {
            return isValid;
        }
        
        public List<String> getErrors() {
            return errors;
        }
    }
}
