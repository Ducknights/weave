package org.example.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * 会话用户DTO
 */

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConversationUserDto {
    private Long id;
    private String name;
    private String avatar;
}