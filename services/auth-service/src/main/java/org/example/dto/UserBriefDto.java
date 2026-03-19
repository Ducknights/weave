package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBriefDto {
    private Long id;
    private String name;
    private String avatar;
    private List<String> roles;
}
