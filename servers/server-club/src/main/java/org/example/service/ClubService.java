package org.example.service;

import org.example.entity.Club;
import org.example.entity.vo.ClubCardVo;

import java.util.List;

public interface ClubService {
    Club createClub(Club club);

    void deleteClub(Integer clubId);

    Club updateClub(Club club);

    List<ClubCardVo> queryClubs();

    Club getClubById(Integer clubId);
}
