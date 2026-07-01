package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.model.dto.ClubBriefDto;
import org.example.model.entity.Club;
import org.example.model.vo.ClubCardVo;

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
