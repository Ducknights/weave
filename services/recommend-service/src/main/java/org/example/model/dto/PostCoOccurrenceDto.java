package org.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostCoOccurrenceDto {
    private Long postA;
    private Long postB;
    private Double coWeight;
}
