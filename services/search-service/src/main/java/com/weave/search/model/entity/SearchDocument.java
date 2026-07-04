package com.weave.search.model.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 搜索文档实体类
 * 映射到 Elasticsearch 的搜索索引
 */
@Data
@Builder
@Document(indexName = "search_documents")
public class SearchDocument {
    
    @Id
    private Long id; // 目标ID
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String title; // 标题
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String content; // 内容
    
    @Field(type = FieldType.Boolean)
    private Boolean isPublic; // 是否公开
}
