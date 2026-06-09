package org.example.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SearchDocumentDto {
    private Long id;
    private String title;
    private String content;
    private Boolean isPublic;
}
