package org.example.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * 搜索文档实体类
 * 映射到 Elasticsearch 的搜索索引
 */
@Data
@Document(indexName = "search_documents")
public class SearchDocument {
    
    @Id
    private String id; // 格式: type_id
    
    @Field(type = FieldType.Keyword)
    private String type; // 内容类型: post, user, comment, club 等
    
    @Field(type = FieldType.Long)
    private Long targetId; // 目标ID
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title; // 标题
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content; // 内容
    
    @Field(type = FieldType.Keyword)
    private String author; // 作者
    
    @Field(type = FieldType.Long)
    private Long authorId; // 作者ID
    
    @Field(type = FieldType.Keyword)
    private String tags; // 标签，逗号分隔
    
    @Field(type = FieldType.Long)
    private Long viewCount; // 浏览量
    
    @Field(type = FieldType.Long)
    private Long likeCount; // 点赞数
    
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt; // 创建时间
    
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt; // 更新时间
    
    @Field(type = FieldType.Boolean)
    private Boolean isPublic; // 是否公开
}
