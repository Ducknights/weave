package org.example.service.imp;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.model.dto.ClubBriefDto;
import org.example.model.entity.Club;
import org.example.model.entity.Member;
import org.example.model.enums.ClubRole;
import org.example.model.enums.MemberStatus;
import org.example.model.vo.ClubCardVo;
import org.example.mapper.ClubMapper;
import org.example.service.ClubService;
import org.example.service.MemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ClubServiceImp extends ServiceImpl<ClubMapper, Club> implements ClubService {

    @Resource
    private ClubMapper clubMapper;
    @Resource
    private MemberService memberService;

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
    public ClubCardVo getClubById(Integer clubId) {
        return clubMapper.getClubVoById(clubId);
    }

    @Override
    @Transactional
    public void joinClub(Integer clubId, Long userId) {
        // 构建成员对象
        Member member = Member.builder()
                .userId(userId.intValue())
                .clubId(clubId)
                .role(ClubRole.MEMBER)
                .status(MemberStatus.ACTIVE)
                .build();
        // 创建成员记录
        memberService.createMember(member);
    }

    @Override
    public Map<Long, ClubBriefDto> batchClubsById(List<Integer> clubIds) {
        List<Club> clubs = listByIds(clubIds);
        return clubs.stream()
                .collect(Collectors.toMap(
                        club -> club.getId().longValue(),
                        club -> ClubBriefDto.builder()
                                .id(club.getId().longValue())
                                .name(club.getName())
                                .description(club.getDescription())
                                .build()
                ));
    }
}
