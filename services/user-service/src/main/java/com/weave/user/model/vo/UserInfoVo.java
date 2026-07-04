package com.weave.user.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.weave.user.model.eunms.GenderEnum;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVo {
    private Long id;
    private String name;
    private String avatar;
    private GenderEnum gender;
    private String motto;
    private Integer fansCont;
    private Integer followCont;
    private Integer postCont;
    private Integer joinedClubCont;
}
