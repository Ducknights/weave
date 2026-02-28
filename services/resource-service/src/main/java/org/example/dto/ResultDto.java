package org.example.dto;

import java.util.List;

public record ResultDto(
        List<FileInfoDto> piles) {
}
