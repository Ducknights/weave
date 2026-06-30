package org.example.model.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.example.model.eunms.GenderEnum;

import java.time.LocalDate;

@Data
@Builder
public class UpdateUserInfoDto {
    private Long id;
    @Size(min = 1, max = 15)
    private String name;
    private String avatar;
    private GenderEnum gender;
    private LocalDate birthday;
    private String address;
    private String motto;
}
