package org.example.service.imp;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.example.entity.Club;
import org.example.entity.vo.ClubCardVo;
import org.example.mapper.ClubMapper;
import org.example.service.ClubService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClubServiceImp implements ClubService {

    @Resource
    private ClubMapper clubMapper;

    @Override
    public Club createClub(Club club) {
        if (clubMapper.insert(club)>0){
            return club;
        };
        return null;
    }

    @Override
    public void deleteClub(Integer clubId) {
        clubMapper.deleteById(clubId);
    }

    @Override
    public Club updateClub(Club club) {
        LambdaUpdateWrapper<Club> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Club::getId,club.getId())
                .set(Club::getName,club.getName())
                .set(Club::getDescription,club.getDescription());
        if(clubMapper.update(updateWrapper)>0){
            return club;
        };
        return null;
    }

    @Override
    public List<ClubCardVo> queryClubs() {
        return clubMapper.queryClubs();
    }

    @Override
    public Club getClubById(Integer clubId) {
        return clubMapper.getClubById(clubId);
    }
}
