package com.weave.club.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.weave.club.model.entity.Club;
import com.weave.model.model.dto.ClubBriefDto;
import com.weave.club.model.vo.ClubCardVo;

import java.util.List;
import java.util.Map;

public interface ClubService extends IService<Club> {
    Club createClub(Club club);

    void deleteClub(Integer clubId);

    Club updateClub(Club club);

    List<ClubCardVo> queryClubs();

    ClubCardVo getClubById(Integer clubId);

    void joinClub(Integer clubId, Long userId);

    Map<Long, ClubBriefDto> batchClubsById(List<Integer> clubIds);
}
